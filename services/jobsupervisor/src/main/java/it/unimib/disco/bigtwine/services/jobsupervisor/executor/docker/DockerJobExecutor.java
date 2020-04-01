package it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;

import java.util.Collections;
import java.util.List;

public class DockerJobExecutor implements JobExecutor<DockerJobProcess, DockerJobExecutable> {

    final private DockerClient dockerClient;
    final private DockerJobExecutorConfig config;

    public DockerJobExecutor(DockerClient dockerClient, DockerJobExecutorConfig config) {
        this.dockerClient = dockerClient;
        this.config = config;
    }

    @Override
    public boolean isRunning(DockerJobProcess process) throws JobExecutorException {
        try {
            List<Container> runningContainers = dockerClient.listContainersCmd()
                .withStatusFilter(Collections.singletonList("running"))
                .withIdFilter(Collections.singletonList(process.getPID()))
                .exec();

            return runningContainers.size() == 1;
        } catch (DockerClientException | DockerException e) {
            throw new JobExecutorException(
                String.format("Cannot check container status: %s. Original Exception %s %s",
                    process.getPID(),
                    e.getClass(),
                    e.getMessage()));
        }
    }

    @Override
    public boolean stop(DockerJobProcess process) throws JobExecutorException {
        if (!this.isRunning(process)) {
            return true;
        }

        try {
            dockerClient
                .stopContainerCmd(process.getPID())
                .exec();

            return true;
        } catch (DockerClientException | DockerException e) {
            return false;
        }
    }

    @Override
    public DockerJobProcess execute(DockerJobExecutable executable) throws JobExecutorException {
        CreateContainerResponse container;
        try {
            container = dockerClient.createContainerCmd(executable.getImageName())
                .withHostConfig(HostConfig.newHostConfig()
                    .withAutoRemove(this.config.getAutoremoveContainer()))
                .withCmd(executable.getContainerCmd())
                .exec();
        } catch (DockerClientException | DockerException e) {
            throw new JobExecutorException(
                String.format("Cannot create container with image: %s, cmd: %s. Original Exception %s %s",
                    executable.getImageName(),
                    executable.getContainerCmd(),
                    e.getClass(),
                    e.getMessage()));
        }

        if (container.getId() == null) {
            throw new JobExecutorException(
                String.format("Container not created with image: %s, cmd: %s.",
                    executable.getImageName(),
                    executable.getContainerCmd()));
        }

        if (this.config.getNetworkId() != null) {
            try {
                dockerClient.connectToNetworkCmd()
                    .withContainerId(container.getId())
                    .withNetworkId(this.config.getNetworkId())
                    .exec();
            } catch (DockerClientException | DockerException e) {
                throw new JobExecutorException(
                    String.format("Cannot connect container to network: %s. Original Exception %s %s",
                        this.config.getNetworkId(),
                        e.getClass(),
                        e.getMessage()));
            }
        }

        try {
            dockerClient.startContainerCmd(container.getId())
                .exec();
        } catch (DockerClientException | DockerException e) {
            throw new JobExecutorException(
                String.format("Cannot start container: %s. Original Exception %s %s",
                    container.getId(),
                    e.getClass(),
                    e.getMessage()));
        }

        return new DockerJobProcess(container.getId());
    }

    @Override
    public boolean test(JobProcess process) {
        return process instanceof DockerJobProcess;
    }

    @Override
    public boolean test(JobExecutable executable) {
        return executable instanceof DockerJobExecutable;
    }
}
