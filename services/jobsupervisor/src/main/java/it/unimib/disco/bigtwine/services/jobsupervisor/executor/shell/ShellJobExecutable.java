package it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;

import java.util.List;

public class ShellJobExecutable implements JobExecutable {
    private List<String> shellCommand;

    public ShellJobExecutable() {
    }

    public List<String> getShellCommand() {
        return shellCommand;
    }

    public void setShellCommand(List<String> shellCommand) {
        this.shellCommand = shellCommand;
    }
}
