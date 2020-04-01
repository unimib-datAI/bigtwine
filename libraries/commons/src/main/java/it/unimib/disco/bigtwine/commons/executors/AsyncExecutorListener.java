package it.unimib.disco.bigtwine.commons.executors;

@FunctionalInterface
public interface AsyncExecutorListener {
    void onCompleted(AsyncExecutor executor, Object... args);
}
