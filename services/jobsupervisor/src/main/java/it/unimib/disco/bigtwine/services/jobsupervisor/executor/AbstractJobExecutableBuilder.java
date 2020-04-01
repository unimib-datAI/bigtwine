package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;

public abstract class AbstractJobExecutableBuilder<E extends JobExecutable> implements JobExecutableBuilder<E> {
    private Job job;

    @Override
    public void setJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
