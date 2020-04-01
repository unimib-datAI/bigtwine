package it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.AbstractJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;

import java.util.ArrayList;
import java.util.List;

public class DockerJobExecutableBuilder extends AbstractJobExecutableBuilder<DockerJobExecutable> {
    private final JobExecutableBuilder.BuilderHelper helper;
    private final DockerJobExecutorConfig config;

    public DockerJobExecutableBuilder(BuilderHelper helper, DockerJobExecutorConfig config) {
        this.helper = helper;
        this.config = config;
    }

    @Override
    public DockerJobExecutable build() throws BuildException {
        if (this.getJob() == null) {
            throw new BuildException("Job is null");
        }

        if (this.getJob().getAnalysis() == null) {
            throw new BuildException("Job's analysis info missing");
        }

        DockerJobExecutable executable = new DockerJobExecutable();
        executable.setContainerCmd(this.buildContainerCommand());
        executable.setImageName(this.config.getImageName());

        return executable;
    }

    private List<String> buildContainerCommand() throws BuildException {
        return this.helper.buildExecutableArgs(this.getJob());
    }
}
