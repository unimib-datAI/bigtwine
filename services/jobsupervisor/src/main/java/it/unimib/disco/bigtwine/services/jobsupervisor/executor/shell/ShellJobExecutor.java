package it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShellJobExecutor implements JobExecutor<ShellJobProcess, ShellJobExecutable> {

    private static final Logger log = LoggerFactory.getLogger(ShellJobExecutor.class);

    private Map<String, Process> processes = new HashMap<>();

    private void validateProcess(ShellJobProcess process) throws JobExecutorException {
        if (!this.processes.containsKey(process.getPID())) {
            throw new JobExecutorException(String.format("Process with pid %s not found", process.getPID()));
        }
    }

    @Override
    public boolean isRunning(ShellJobProcess process) throws JobExecutorException {
        this.validateProcess(process);

        Process nativeProcess = this.processes.get(process.getPID());
        return nativeProcess.isAlive();
    }

    @Override
    public boolean stop(ShellJobProcess process) throws JobExecutorException {
        this.validateProcess(process);

        Process nativeProcess = this.processes.get(process.getPID());
        nativeProcess.destroy();
        return true;
    }

    @Override
    public ShellJobProcess execute(ShellJobExecutable executable) throws JobExecutorException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(executable.getShellCommand());

        try {
            Process process = builder.start();
            String pid = UUID.randomUUID().toString();
            this.processes.put(pid, process);

            return new ShellJobProcess(pid);
        } catch (IOException e) {
            log.error("Cannot run executable", e);
            throw new JobExecutorException(String.format("Cannot run executable: %s", e.getLocalizedMessage()));
        }
    }

    @Override
    public boolean test(JobProcess process) {
        return process instanceof ShellJobProcess;
    }

    @Override
    public boolean test(JobExecutable executable) {
        return executable instanceof ShellJobExecutable;
    }
}
