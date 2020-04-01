package it.unimib.disco.bigtwine.commons.executors;

import java.io.File;

public interface PerpetualFileExecutor extends PerpetualExecutor {
    void setInputWorkingDirectory(File in);
    File getInputWorkingDirectory();
    void setOutputWorkingDirectory(File out);
    File getOutputWorkingDirectory();
}
