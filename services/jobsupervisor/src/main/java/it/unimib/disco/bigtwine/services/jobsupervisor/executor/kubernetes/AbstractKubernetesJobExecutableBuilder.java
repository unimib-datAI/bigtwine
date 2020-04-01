package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import io.kubernetes.client.models.V1Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.AbstractJobExecutableBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractKubernetesJobExecutableBuilder extends AbstractJobExecutableBuilder<KubernetesJobExecutable> {
    protected final KubernetesObjectLoader kubernetesObjectLoader;

    private Job job;

    public AbstractKubernetesJobExecutableBuilder(KubernetesObjectLoader kubernetesObjectLoader) {
        this.kubernetesObjectLoader = kubernetesObjectLoader;
    }

    public Job getJob() {
        return job;
    }

    @Override
    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public KubernetesJobExecutable build() throws BuildException {
        if (this.getJob() == null) {
            throw new BuildException("Job is null");
        }

        if (this.getJob().getAnalysis() == null) {
            throw new BuildException("Job's analysis info missing");
        }

        Object k8sObject = this.buildKubernetesObject();
        KubernetesJobExecutable executable = new KubernetesJobExecutable();
        executable.setKubernetesObjectSpec(k8sObject);

        return executable;
    }

    protected String buildKubernetesObjectName() {
        AnalysisInfo analysis = this.getJob().getAnalysis();
        return String.format("streamprocessor-%s", this.getJob().getId());
    }

    protected Object buildKubernetesObject() throws BuildException {
        String jobName = this.buildKubernetesObjectName();
        List<String> command = this.buildExecutableCommand();
        List<String> args = this.buildExecutableArgs();

        Map<String, Object> vars = new HashMap<>();
        vars.put("metadata.name", jobName);
        vars.put("metadata.labels.job-id", job.getId());
        vars.put("metadata.labels.job-type", job.getJobType());
        vars.put("metadata.labels.analysis-id", job.getAnalysis().getId());
        vars.put("metadata.labels.analysis-type", job.getAnalysis().getType());

        if (command != null) {
            vars.put("spec.template.spec.containers[0].command", command);
        }

        if (args != null) {
            vars.put("spec.template.spec.containers[0].args", args);
        }

        Object k8sObject;
        try {
            k8sObject = this.kubernetesObjectLoader.getKubernetesObjectSpec(vars, V1Job.class);
        } catch (Exception e) {
            throw new BuildException("Cannot build k8s object spec: " + e.getLocalizedMessage());
        }

        if (k8sObject == null) {
            throw new BuildException("Cannot build k8s object spec");
        }

        return k8sObject;
    }

    protected abstract List<String> buildExecutableCommand() throws BuildException;
    protected abstract List<String> buildExecutableArgs() throws BuildException;
}
