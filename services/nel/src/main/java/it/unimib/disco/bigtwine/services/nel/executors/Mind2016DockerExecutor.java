package it.unimib.disco.bigtwine.services.nel.executors;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.docker.DockerSyncExecutor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Mind2016DockerExecutor extends DockerSyncExecutor implements SyncFileExecutor {
    public static final String DOCKER_IMAGE = "bigtwine-tool-nel";

    private String kbPath;

    protected Mind2016DockerExecutor(String dockerImage) {
        super(dockerImage);
    }

    public Mind2016DockerExecutor() {
        this(DOCKER_IMAGE);
    }

    @Override
    public String getExecutorId() {
        return "docker-mind2016";
    }

    public String getKnowledgeBasePath() {
        return this.kbPath;
    }

    public void setKnowledgeBasePath(String knowledgeBasePath) {
        this.kbPath = knowledgeBasePath;
    }

    @Override
    protected void validateExecuteArgs(Object... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                String.format("Sync executor expect 2 arguments, %d given", args.length));
        }

        if (!(args[0] instanceof File && args[1] instanceof File)) {
            throw new IllegalArgumentException(
                String.format("Sync executor expect 2 arguments of type File (%s, %s given)",
                    args[0].getClass(), args[1].getClass()));
        }
    }

    @Override
    protected String[] buildContainerCommand(String... additionalArgs) {
        List<String> cmd = Arrays.asList("java", "-jar", "NEEL_Linking.jar");
        cmd.addAll(Arrays.asList(additionalArgs));

        return cmd.toArray(new String[0]);
    }

    @Override
    protected String[] prepareAdditionalContainerArgs(Object... additionalArgs) {
        return new String[] {
            "/data/input",
            this.getKnowledgeBasePath(),
            "/data/output"
        };
    }

    private File getInputDirectoryFromArgs(Object... args) {
        return ((File)args[0]);
    }

    private File getOutputDirectoryFromArgs(Object... args) {
        return ((File)args[1]);
    }

    @Override
    protected void configureContainer(CreateContainerCmd containerCmd, Object... args) {
        this.setAutoRemove(containerCmd, true);
        this.bindVolumes(
            containerCmd,
            new Bind(this.getInputDirectoryFromArgs(args).getAbsolutePath(), new Volume("/data/input")),
            new Bind(this.getOutputDirectoryFromArgs(args).getAbsolutePath(), new Volume("/data/output"))
        );
    }

    @Override
    public String execute(File inputFile, File outputFile) {
        return this.execute((Object)inputFile, outputFile);
    }
}
