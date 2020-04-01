package it.unimib.disco.bigtwine.services.analysis.client;

import it.unimib.disco.bigtwine.services.analysis.domain.CronEntryInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="cronscheduler")
public interface CronSchedulerServiceClient {
    @RequestMapping(value = "/api/cron-entries", method = RequestMethod.POST)
    ResponseEntity<CronEntryInfo> createCronEntry(CronEntryInfo entry);
}
