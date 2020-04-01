package it.unimib.disco.bigtwine.commons.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;

public interface GenericProcessor<I, O> {
    String getProcessorId();
    void setExecutor(Executor executor);
    Executor getExecutor();
    void setListener(ProcessorListener<O> listener);

    boolean configureProcessor();

    boolean process(String tag, I item);
    boolean process(String tag, I[] items);
}
