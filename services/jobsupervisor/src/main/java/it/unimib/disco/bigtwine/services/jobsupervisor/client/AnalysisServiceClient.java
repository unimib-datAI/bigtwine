package it.unimib.disco.bigtwine.services.jobsupervisor.client;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="analysis")
public interface AnalysisServiceClient {
    @RequestMapping("/api/public/analyses/{id}")
    AnalysisInfo findAnalysisById(@PathVariable(value="id") String id);
}
