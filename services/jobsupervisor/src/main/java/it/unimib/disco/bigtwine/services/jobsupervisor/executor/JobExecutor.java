package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

public interface JobExecutor<P extends JobProcess, E extends JobExecutable> {
    boolean isRunning(P process) throws JobExecutorException;
    boolean stop(P process) throws JobExecutorException;
    P execute(E executable) throws JobExecutorException;
    boolean test(JobProcess process);
    boolean test(JobExecutable executable);

    class JobExecutorException extends Exception {
        public JobExecutorException() {
        }

        public JobExecutorException(String message) {
            super(message);
        }
    }
}
