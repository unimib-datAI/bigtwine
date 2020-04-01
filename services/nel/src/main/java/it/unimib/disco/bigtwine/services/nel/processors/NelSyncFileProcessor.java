package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.*;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.file.SyncFileProcessor;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class NelSyncFileProcessor extends NelFileProcessor implements SyncFileProcessor<RecognizedText> {

    protected SyncFileExecutor executor;

    public NelSyncFileProcessor(InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder, SyncFileExecutor executor) {
        super(inputProducerBuilder, outputParserBuilder);
        this.setExecutor(executor);
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof SyncFileExecutor)) {
            throw new IllegalArgumentException("Unsupported executor type");
        }
        this.executor = (SyncFileExecutor)executor;
    }

    @Override
    public SyncExecutor getSyncExecutor() {
        return this.executor;
    }

    @Override
    public SyncFileExecutor getSyncFileExecutor() {
        return this.executor;
    }

    @Override
    public boolean configureProcessor() {
        this.processorId = RandomStringUtils.randomAlphanumeric(16);
        this.inputDirectory = Paths.get(this.getWorkingDirectory().toString(), this.getProcessorId(), "input").toFile();
        this.outputDirectory = Paths.get(this.getWorkingDirectory().toString(), this.getProcessorId(), "output").toFile();

        return this.setupWorkingDirectory();
    }

    @Override
    public boolean process(String tag, RecognizedText[] tweets) {
        File inputFile = this.makeInputFile(tag);
        File outputFile = this.makeOutputFile(tag);

        boolean res = this.generateInputFile(inputFile, tweets);

        if (!res) {
            return false;
        }

        try {
            if (!outputFile.createNewFile()) {
                return false;
            }
        } catch (IOException|SecurityException e) {
            return false;
        }

        res = this.getSyncFileExecutor().execute(inputFile, outputFile) != null;

        if (!res) {
            return false;
        }

        this.processOutputFile(outputFile);

        return true;
    }
}
