package it.unimib.disco.bigtwine.commons.executors;

import java.io.File;

public interface SyncFileExecutor extends SyncExecutor {
    String execute(File inputFile, File outputFile);
}
