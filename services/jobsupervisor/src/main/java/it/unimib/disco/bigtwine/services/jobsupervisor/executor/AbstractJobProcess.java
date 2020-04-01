package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJobProcess implements JobProcess {

    private String pid;

    private Map<String, ?> extraData = new HashMap<>();

    protected AbstractJobProcess(String pid) {
        this.pid = pid;
    }

    @Override
    public String getPID() {
        return this.pid;
    }

    @Override
    public Map<String, ?> getExtraData() {
        return this.extraData;
    }
}
