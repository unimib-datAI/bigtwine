package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;

import java.util.List;

public interface JobExecutableBuilder<E extends JobExecutable> {
    void setJob(Job job);
    E build() throws BuildException;

    class BuildException extends Exception {
        public BuildException(String message) {
            super(message);
        }
    }

    interface BuilderHelper {
        List<String> buildExecutableCommand(Job job) throws BuildException;
        List<String> buildExecutableArgs(Job job) throws BuildException;
    }
}
