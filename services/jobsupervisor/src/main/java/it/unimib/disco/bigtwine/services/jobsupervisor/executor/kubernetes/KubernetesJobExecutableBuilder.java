package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;

import java.util.*;

public class KubernetesJobExecutableBuilder extends AbstractKubernetesJobExecutableBuilder {

    private final JobExecutableBuilder.BuilderHelper helper;

    public KubernetesJobExecutableBuilder(
        KubernetesObjectLoader kubernetesObjectLoader,
        JobExecutableBuilder.BuilderHelper helper) {
        super(kubernetesObjectLoader);
        this.helper = helper;
    }

    @Override
    protected List<String> buildExecutableCommand() throws BuildException {
        return null;
    }

    @Override
    protected List<String> buildExecutableArgs() throws BuildException {
        return this.helper.buildExecutableArgs(this.getJob());
    }
}
