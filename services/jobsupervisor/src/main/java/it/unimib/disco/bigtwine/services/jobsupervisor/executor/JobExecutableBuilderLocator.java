package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;

public interface JobExecutableBuilderLocator {
    JobExecutableBuilder<?> getJobExecutableBuilder(Job job);

    class JobExecutableBuilderNotFoundException extends IllegalArgumentException {
        public JobExecutableBuilderNotFoundException(String s) {
            super(s);
        }
    }
}
