package it.unimib.disco.bigtwine.commons.executors;

public interface PerpetualExecutor extends Executor {
    void run();
    void stop();
    boolean isRunning();
}
