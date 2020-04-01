package it.unimib.disco.bigtwine.commons.processors;

@FunctionalInterface
public interface ProcessorListener<O> {
    void onProcessed(GenericProcessor processor, String tag, O[] processedItems);
}
