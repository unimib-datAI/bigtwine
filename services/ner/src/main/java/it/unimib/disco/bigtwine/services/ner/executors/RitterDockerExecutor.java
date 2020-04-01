package it.unimib.disco.bigtwine.services.ner.executors;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import it.unimib.disco.bigtwine.commons.executors.PerpetualFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.docker.DockerPerpetualExecutor;

import java.io.File;

public class RitterDockerExecutor extends DockerPerpetualExecutor implements PerpetualFileExecutor {
    public static final String DOCKER_IMAGE = "bigtwine-tool-ner";

    private File inputWorkingDirectory;
    private File outputWorkingDirectory;

    protected RitterDockerExecutor(String dockerImage) {
        super(dockerImage);
    }

    public RitterDockerExecutor() {
        this(DOCKER_IMAGE);
    }

    @Override
    public String getExecutorId() {
        return "docker-ritter";
    }

    @Override
    public void setInputWorkingDirectory(File inputWorkingDirectory) {
        this.inputWorkingDirectory = inputWorkingDirectory;
    }

    @Override
    public File getInputWorkingDirectory() {
        return inputWorkingDirectory;
    }

    public void setOutputWorkingDirectory(File outputWorkingDirectory) {
        this.outputWorkingDirectory = outputWorkingDirectory;
    }

    @Override
    public File getOutputWorkingDirectory() {
        return outputWorkingDirectory;
    }


    @Override
    protected String[] buildContainerCommand(String... additionalArgs) {
        return new String[0];
    }

    @Override
    protected String[] prepareAdditionalContainerArgs(Object... additionalArgs) {
        return new String[0];
    }

    @Override
    protected void validateExecuteArgs(Object... args) {
        if (args.length != 0) {
            throw new IllegalArgumentException("No arguments accepted");
        }
    }

    @Override
    protected void configureContainer(CreateContainerCmd containerCmd, Object... args) {
        this.setAutoRemove(containerCmd, true);
        this.bindVolumes(
            containerCmd,
            new Bind(this.getInputWorkingDirectory().getAbsolutePath(), new Volume("/data/input")),
            new Bind(this.getOutputWorkingDirectory().getAbsolutePath(), new Volume("/data/output"))
        );
    }
}
