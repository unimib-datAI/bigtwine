package it.unimib.disco.bigtwine.commons.processors;

import it.unimib.disco.bigtwine.commons.executors.SyncExecutor;

public interface SyncProcessor {
    SyncExecutor getSyncExecutor();
}
