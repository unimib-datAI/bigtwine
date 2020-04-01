package it.unimib.disco.bigtwine.commons.executors.docker;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import it.unimib.disco.bigtwine.commons.executors.PerpetualExecutor;

public abstract class DockerPerpetualExecutor extends DockerExecutor implements PerpetualExecutor {

    public DockerPerpetualExecutor(String dockerImage) {
        super(dockerImage);
    }

    @Override
    public void run() {
        CreateContainerCmd containerCmd = this.createContainer(this.getDockerImage());
        this.configureContainer(containerCmd);
        CreateContainerResponse container = this.runContainer(containerCmd);
        this.containerId = container.getId();
    }

    @Override
    public void stop() {
        if (this.containerId == null) {
            return;
        }

        this.getDockerClient().stopContainerCmd(this.containerId);
        this.getDockerClient().removeContainerCmd(this.containerId);
    }

    @Override
    public boolean isRunning() {
        if (this.containerId == null) {
            return false;
        }

        InspectContainerResponse containerInfo = this.getDockerClient()
                .inspectContainerCmd(this.containerId)
                .exec();

        if (containerInfo == null) {
            return false;
        }

        InspectContainerResponse.ContainerState state = containerInfo.getState();
        if (state == null) {
            return false;
        }

        if (state.getRunning() == null) {
            return false;
        }

        return state.getRunning();
    }
}
