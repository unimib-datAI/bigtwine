package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.PerpetualFileExecutor;
import it.unimib.disco.bigtwine.services.nel.Linker;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;

public final class Mind2016PerpetualProcessor extends NelPerpetualFileProcessor {

    public static final Linker linker = Linker.mind2016;

    public Mind2016PerpetualProcessor(InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder, PerpetualFileExecutor executor) {
        super(inputProducerBuilder, outputParserBuilder, executor);
    }

    @Override
    public Linker getLinker() {
        return linker;
    }
}
