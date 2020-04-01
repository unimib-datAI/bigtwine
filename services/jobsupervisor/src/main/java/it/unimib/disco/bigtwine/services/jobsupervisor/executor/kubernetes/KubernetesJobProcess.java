package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.AbstractJobProcess;

public class KubernetesJobProcess extends AbstractJobProcess {

    final private String apiVersion;
    final private String kind;
    final private String name;

    public KubernetesJobProcess(String apiVersion, String kind, String name) {
        super(String.format("%s/%s", kind, name));

        this.apiVersion = apiVersion;
        this.kind = kind;
        this.name = name;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }
}
