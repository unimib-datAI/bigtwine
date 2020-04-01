package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;
import it.unimib.disco.bigtwine.services.nel.Linker;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;

public final class Mind2016SyncProcessor extends NelSyncFileProcessor {

    public static final Linker linker = Linker.mind2016;

    public Mind2016SyncProcessor(SyncFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        super(inputProducerBuilder, outputParserBuilder, executor);
    }

    @Override
    public Linker getLinker() {
        return linker;
    }
}
