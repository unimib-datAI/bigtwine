package it.unimib.disco.bigtwine.commons.executors;

import java.io.File;

public class NopExecutor implements PerpetualFileExecutor {

    private File inputWorkingDirectory;
    private File outputWorkingDirectory;
    private boolean isRunning;

    @Override
    public String getExecutorId() {
        return "nop";
    }

    @Override
    public File getInputWorkingDirectory() {
        return inputWorkingDirectory;
    }

    @Override
    public void setInputWorkingDirectory(File inputWorkingDirectory) {
        this.inputWorkingDirectory = inputWorkingDirectory;
    }

    @Override
    public File getOutputWorkingDirectory() {
        return outputWorkingDirectory;
    }

    @Override
    public void setOutputWorkingDirectory(File outputWorkingDirectory) {
        this.outputWorkingDirectory = outputWorkingDirectory;
    }

    @Override
    public void run() {
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }
}
