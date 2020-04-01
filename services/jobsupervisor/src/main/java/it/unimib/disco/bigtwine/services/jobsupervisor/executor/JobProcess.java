package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import java.util.Map;

public interface JobProcess {
    String getPID();
    Map<String, ?> getExtraData();
}
