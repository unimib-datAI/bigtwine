package it.unimib.disco.bigtwine.commons.executors.docker;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import it.unimib.disco.bigtwine.commons.executors.SyncExecutor;

public abstract class DockerSyncExecutor extends DockerExecutor implements SyncExecutor {

    public DockerSyncExecutor(String dockerImage) {
        super(dockerImage);
    }

    protected String execute(Object... args) {
        this.validateExecuteArgs(args);
        String[] additionalArgs = this.prepareAdditionalContainerArgs(args);
        String[] cmd = this.buildContainerCommand(additionalArgs);
        CreateContainerCmd containerCmd = this.createContainer(this.getDockerImage(), cmd);
        this.configureContainer(containerCmd, args);
        CreateContainerResponse container = this.runContainer(containerCmd);

        return this.getContainerLogs(container);
    }
}
