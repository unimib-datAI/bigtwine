package it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.AbstractJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShellJobExecutableBuilder extends AbstractJobExecutableBuilder<ShellJobExecutable> {

    private final JobExecutableBuilder.BuilderHelper helper;

    public ShellJobExecutableBuilder(JobExecutableBuilder.BuilderHelper helper) {
        this.helper = helper;
    }

    @Override
    public ShellJobExecutable build() throws BuildException {
        List<String> cmd = this.helper.buildExecutableCommand(this.getJob());
        List<String> args = this.helper.buildExecutableArgs(this.getJob());

        ArrayList<String> shellCmd = new ArrayList<>();
        shellCmd.addAll(cmd);
        shellCmd.addAll(args);

        ShellJobExecutable executable = new ShellJobExecutable();
        executable.setShellCommand(shellCmd);

        return executable;
    }
}
