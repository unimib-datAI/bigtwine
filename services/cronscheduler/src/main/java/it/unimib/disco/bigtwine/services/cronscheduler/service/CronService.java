package it.unimib.disco.bigtwine.services.cronscheduler.service;

import it.unimib.disco.bigtwine.commons.messaging.CronTaskCompletionEvent;
import it.unimib.disco.bigtwine.commons.messaging.CronTaskEvent;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronEntry;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronSchedule;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronTask;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronStatus;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronTaskStatus;
import it.unimib.disco.bigtwine.services.cronscheduler.messaging.CronTaskCompletionConsumerChannel;
import it.unimib.disco.bigtwine.services.cronscheduler.messaging.CronTaskProducerChannel;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronEntryRepository;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronScheduleRepository;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

@Service
public class CronService {

    private final Logger log = LoggerFactory.getLogger(CronService.class);

    private final static int MIN_SCHEDULED_CRON = 5;
    private final static int CRON_PARALLELISM_AUTO = 0;
    private final static int CRON_SCHEDULES_MAX_DAYS = 7;

    private final CronEntryRepository cronEntryRepository;
    private final CronScheduleRepository cronScheduleRepository;
    private final CronTaskRepository cronTaskRepository;
    private final DiscoveryClient discoveryClient;
    private final MessageChannel cronTaskChannel;
    private final MessageChannel cronTaskCompletionChannel;

    public CronService(
        CronEntryRepository cronEntryRepository,
        CronScheduleRepository cronScheduleRepository,
        CronTaskRepository cronTaskRepository,
        CronTaskProducerChannel cronTaskProducerChannel,
        CronTaskCompletionConsumerChannel cronTaskCompletionConsumerChannel,
        DiscoveryClient discoveryClient) {
        this.cronEntryRepository = cronEntryRepository;
        this.cronScheduleRepository = cronScheduleRepository;
        this.cronTaskRepository = cronTaskRepository;
        this.cronTaskChannel = cronTaskProducerChannel.cronTaskChannel();
        this.cronTaskCompletionChannel = cronTaskCompletionConsumerChannel.cronTaskCompletionChannel();
        this.discoveryClient = discoveryClient;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleCronEntries() {
        List<CronEntry> cronEntries = this.cronEntryRepository.findAllByActive(true);
        for (CronEntry entry: cronEntries) {
            long count = this.cronScheduleRepository.countAllByEntryIdAndStatus(entry.getId(), CronStatus.SCHEDULED);
            if (count < MIN_SCHEDULED_CRON) {
                CronSequenceGenerator generator = new CronSequenceGenerator(entry.getCronExpr());
                Date date;

                if (count > 0) {
                    Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "scheduledDate"));
                    CronSchedule lastCron = this.cronScheduleRepository
                        .findAllByEntryIdAndStatus(entry.getId(), CronStatus.SCHEDULED, page)
                        .get(0);

                    date = Date.from(lastCron.getScheduledDate());
                } else {
                    date = new Date();
                }

                int tasksCount = this.getCronEntryTasksCount(entry);
                for (long i = count; i < MIN_SCHEDULED_CRON; ++i) {
                    date = generator.next(date);
                    this.scheduleCron(entry, Instant.now(), tasksCount);
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void executeCronSchedules() {
        List<CronEntry> cronEntries = this.cronEntryRepository.findAllByActive(true);
        for (CronEntry entry: cronEntries) {
            Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "scheduledDate"));
            List<CronSchedule> nextCrons = this.cronScheduleRepository
                .findAllByEntryIdAndStatus(entry.getId(), CronStatus.SCHEDULED, page);

            if (nextCrons.size() == 0) {
                continue;
            }

            CronSchedule nextCron = nextCrons.get(0);
            if (nextCron.getScheduledDate().isBefore(Instant.now())) {
                Instant now = Instant.now();
                nextCron.setStatus(CronStatus.RUNNING);
                nextCron.setExecutedDate(now);
                nextCron.setUpdatedDate(now);
                nextCron = this.cronScheduleRepository.save(nextCron);

                this.runCronSchedule(nextCron);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanOldCronSchedules() {
        Instant date = Instant.now().minus(CRON_SCHEDULES_MAX_DAYS, ChronoUnit.DAYS);
        this.cronScheduleRepository.findAllByUpdatedDateBefore(date).forEach((cronSchedule -> {
            this.cronTaskRepository.deleteAllByScheduleId(cronSchedule.getId());
            this.cronScheduleRepository.delete(cronSchedule);
        }));
    }

    @StreamListener(CronTaskCompletionConsumerChannel.CHANNEL)
    public void cronTaskCompletionReceived(CronTaskCompletionEvent event) {
        CronTask task = this.cronTaskRepository
            .findByScheduleIdAndTask(event.getCronScheduleId(), event.getTask())
            .orElse(null);

        if (task == null) {
            log.error("Cron task not found: invalid cronScheduleId or number {} {}", event.getCronScheduleId(), event.getTask());
            return;
        }

        task.setStatus((event.isFailed()) ? CronTaskStatus.FAILED : CronTaskStatus.COMPLETED);
        task.setFinishedDate(event.getFinishedDate());
        task.setMessage(event.getMessage());

        this.cronTaskRepository.save(task);
        this.updateCronSchedule(event.getCronScheduleId());
    }

    @Transactional
    public void updateCronSchedule(String cronScheduleId) {
        CronSchedule cronSchedule = this.cronScheduleRepository.findById(cronScheduleId).orElse(null);

        if (cronSchedule == null) {
            log.error("Cron schedule not found: invalid cronScheduleId {}", cronScheduleId);
            return;
        }

        List<CronTask> tasks = this.cronTaskRepository.findAllByScheduleId(cronScheduleId);

        int completedTasks = 0, failedTasks = 0;
        for (CronTask task : tasks) {
            if (task.getStatus() == CronTaskStatus.COMPLETED) {
                completedTasks++;
            } else if (task.getStatus() == CronTaskStatus.FAILED) {
                failedTasks++;
            }
        }

        if ((completedTasks + failedTasks) == cronSchedule.getTasksCount()) {
            CronStatus status;
            if (failedTasks == 0) {
                status = CronStatus.SUCCESS;
            } else if (failedTasks == cronSchedule.getTasksCount()) {
                status = CronStatus.FAILED;
            } else {
                status = CronStatus.PARTIAL;
            }

            Instant now = Instant.now();
            cronSchedule.setStatus(status);
            cronSchedule.setFinishedDate(now);
            cronSchedule.setUpdatedDate(now);
            this.cronScheduleRepository.save(cronSchedule);

            CronEntry entry = cronSchedule.getEntry();
            entry.setLastRun(now);
            this.cronEntryRepository.save(entry);
        }
    }

    private void scheduleCron(CronEntry entry, Instant date, int tasksCount) {
        if (tasksCount <= 0) {
            throw new IllegalArgumentException("tasksCount must be greater than 0");
        }

        Instant now = Instant.now();
        CronSchedule newSchedule = new CronSchedule()
            .entry(entry)
            .status(CronStatus.SCHEDULED)
            .tasksCount(tasksCount)
            .createdDate(now)
            .updatedDate(now)
            .scheduledDate(date);

        this.cronScheduleRepository.save(newSchedule);
    }

    private int getCronEntryTasksCount(CronEntry entry) {
        int tasksCount = entry.getParallelism();
        if (tasksCount == CRON_PARALLELISM_AUTO) {
            tasksCount = this.discoveryClient.getInstances(entry.getService()).size();
            if (tasksCount == 0) {
                tasksCount = 1;
            }
        }

        return tasksCount;
    }

    private void runCronSchedule(CronSchedule nextCron) {
        for (int taskNum = 1; taskNum <= nextCron.getTasksCount(); ++taskNum) {
            CronTask task = new CronTask()
                .schedule(nextCron)
                .task(taskNum)
                .executedDate(Instant.now())
                .status(CronTaskStatus.RUNNING);

            this.cronTaskRepository.save(task);

            CronTaskEvent event = new CronTaskEvent();
            event.setCronScheduleId(nextCron.getId());
            event.setService(nextCron.getEntry().getService());
            event.setGroup(nextCron.getEntry().getGroup());
            event.setName(nextCron.getEntry().getName());
            event.setTask(taskNum);
            event.setTasksCount(nextCron.getTasksCount());
            event.setExecutedDate(nextCron.getExecutedDate());

            Message<CronTaskEvent> message = MessageBuilder
                .withPayload(event)
                .build();

            this.cronTaskChannel.send(message);
        }
    }
}
