package it.unimib.disco.bigtwine.services.cronscheduler.service;

import it.unimib.disco.bigtwine.services.cronscheduler.CronschedulerApp;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronEntry;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronSchedule;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronStatus;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronEntryRepository;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronScheduleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CronschedulerApp.class)
public class CronServiceTest {

    @Autowired
    private CronScheduleRepository cronScheduleRepository;

    @Autowired
    private CronEntryRepository cronEntryRepository;

    @Autowired
    private CronService cronService;

    public CronServiceTest() {
    }

    @Before
    public void setUp() {
        this.cronEntryRepository.save(new CronEntry()
            .service("test")
            .group("default")
            .name("test")
            .parallelism(1)
            .active(true)
            .cronExpr("0 * * * * *"));
    }

    @Test
    public void scheduleCronEntries() {
        long count = this.cronScheduleRepository.count();
        assertThat(count, is(0L));
        this.cronService.scheduleCronEntries();
        count = this.cronScheduleRepository.count();
        assertThat(count, is(5L));

        List<CronSchedule> crons = this.cronScheduleRepository.findAll(Sort.by(Sort.Direction.ASC, "scheduledDate"));
        assertThat(crons.get(0).getStatus(), is(CronStatus.SCHEDULED));
        long diff = crons.get(1).getScheduledDate().toEpochMilli() - crons.get(0).getScheduledDate().toEpochMilli();
        assertTrue(diff > 0);
        assertTrue(diff > 59000 && diff < 61000);
    }

    @Test
    public void executeCronSchedules() {
        CronSchedule scheduledCron = this.cronScheduleRepository.save(new CronSchedule()
            .entry(this.cronEntryRepository.findAll().get(0))
            .status(CronStatus.SCHEDULED)
            .scheduledDate(Instant.now().minusSeconds(1)));

        this.cronService.executeCronSchedules();

        scheduledCron = this.cronScheduleRepository
            .findById(scheduledCron.getId())
            .orElseThrow(RuntimeException::new);

        assertThat(scheduledCron.getStatus(), is(CronStatus.RUNNING));
        assertThat(scheduledCron.getExecutedDate(), notNullValue());
    }
}
