package it.unimib.disco.bigtwine.commons.executors.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import it.unimib.disco.bigtwine.commons.executors.Executor;


public abstract class DockerExecutor implements Executor {

    private DockerClient dockerClient = null;
    private String dockerImage;
    protected String containerId;

    protected DockerExecutor(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    protected abstract String[] buildContainerCommand(String... additionalArgs);
    protected abstract String[] prepareAdditionalContainerArgs(Object... additionalArgs);
    protected abstract void validateExecuteArgs(Object... args);

    protected DockerClient getDockerClient() {
        if (dockerClient == null) {
            DockerClientConfig config = DefaultDockerClientConfig
                    .createDefaultConfigBuilder()
                    .build();
            this.dockerClient = DockerClientBuilder
                    .getInstance(config)
                    .build();
        }

        return this.dockerClient;
    }

    protected String getDockerImage() {
        return this.dockerImage;
    }

    protected CreateContainerCmd createContainer(String image, String... cmd) {
        return dockerClient
                .createContainerCmd(image)
                .withCmd(cmd);
    }

    protected abstract void configureContainer(CreateContainerCmd containerCmd, Object... args);

    protected CreateContainerResponse runContainer(CreateContainerCmd containerCmd) {
        CreateContainerResponse container = containerCmd.exec();
        this.getDockerClient()
                .startContainerCmd(container.getId())
                .exec();

        return container;
    }

    protected CreateContainerCmd setAutoRemove(CreateContainerCmd containerCmd, boolean autoRemove) {
        final HostConfig hostConfig = (containerCmd.getHostConfig() == null) ?
                HostConfig.newHostConfig() :
                containerCmd.getHostConfig();

        return containerCmd.withHostConfig(hostConfig.withAutoRemove(autoRemove));
    }

    protected CreateContainerCmd bindVolumes(CreateContainerCmd containerCmd, Bind... binds) {
        final HostConfig hostConfig = (containerCmd.getHostConfig() == null) ?
                HostConfig.newHostConfig() :
                containerCmd.getHostConfig();

        return containerCmd.withHostConfig(hostConfig.withBinds(binds));
    }

    protected String getContainerLogs(String containerId) {
        LogContainerCmd logContainerCmd = this.getDockerClient().logContainerCmd(containerId);
        logContainerCmd
                .withStdOut(true)
                .withStdErr(false);

        final StringBuilder logs = new StringBuilder();

        try {
            logContainerCmd.exec(new LogContainerResultCallback() {
                @Override
                public void onNext(Frame item) {
                    logs.append(item.toString());
                    logs.append('\n');
                }
            }).awaitCompletion();
        } catch (InterruptedException e) {
        }finally {
            this.getDockerClient().removeContainerCmd(containerId);
        }

        return logs.toString();
    }

    protected String getContainerLogs(CreateContainerResponse container) {
        return this.getContainerLogs(container.getId());
    }
}
