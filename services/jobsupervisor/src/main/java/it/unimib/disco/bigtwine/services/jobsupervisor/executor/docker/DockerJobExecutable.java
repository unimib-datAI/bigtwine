package it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;

import java.util.List;

public class DockerJobExecutable implements JobExecutable {
    private String imageName;
    private List<String> containerCmd;

    public DockerJobExecutable() {
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public List<String> getContainerCmd() {
        return containerCmd;
    }

    public void setContainerCmd(List<String> containerCmd) {
        this.containerCmd = containerCmd;
    }
}
