package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;

public class KubernetesJobExecutable implements JobExecutable {
    private Object kubernetesObjectSpec;

    public KubernetesJobExecutable() {
    }

    public Object getKubernetesObjectSpec() {
        return kubernetesObjectSpec;
    }

    public void setKubernetesObjectSpec(Object kubernetesObjectSpec) {
        this.kubernetesObjectSpec = kubernetesObjectSpec;
    }
}
