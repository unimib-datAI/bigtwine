package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.services.analysis.client.CronSchedulerServiceClient;
import it.unimib.disco.bigtwine.services.analysis.domain.CronEntryInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CronEntryRegistry {
    public static final int REGISTER_MAX_RETRY = 3;
    public static final int REGISTER_ATTEMPT_DELAY = 30000;

    private final CronSchedulerServiceClient cronSchedulerServiceClient;
    private List<CronEntryAttempt> queue = new ArrayList<>();

    public CronEntryRegistry(CronSchedulerServiceClient cronSchedulerServiceClient) {
        this.cronSchedulerServiceClient = cronSchedulerServiceClient;
    }

    public void registerCronEntry(CronEntryInfo entry) {
        this.registerCronEntry(entry, REGISTER_MAX_RETRY);
    }

    public void registerCronEntry(CronEntryInfo entry, int maxRetry) {
        this.queue.add(new CronEntryAttempt(maxRetry, entry));
    }

    @Scheduled(fixedRate = REGISTER_ATTEMPT_DELAY)
    public void attemptRegisterQueuedCronEntries() {
        if (queue.size() == 0) {
            return;
        }

        List<CronEntryAttempt> newQueue = new ArrayList<>();
        for (CronEntryAttempt attempt: queue) {
            try {
                ResponseEntity<CronEntryInfo> res = this.cronSchedulerServiceClient
                    .createCronEntry(attempt.getCronEntry());
                if (res.getStatusCode() != HttpStatus.CREATED) {
                    throw new Exception("Entity not created");
                }
            } catch (Exception e) {
                if (attempt.getRemainingAttempts() > 1) {
                    newQueue.add(new CronEntryAttempt(attempt.getRemainingAttempts() - 1, attempt.getCronEntry()));
                }
            }
        }

        this.queue = newQueue;
    }

    private static class CronEntryAttempt {
        private int remainingAttempts;
        private CronEntryInfo cronEntry;

        public CronEntryAttempt(int remainingAttempts, CronEntryInfo cronEntry) {
            this.remainingAttempts = remainingAttempts;
            this.cronEntry = cronEntry;
        }

        public int getRemainingAttempts() {
            return remainingAttempts;
        }

        public CronEntryInfo getCronEntry() {
            return cronEntry;
        }
    }
}
