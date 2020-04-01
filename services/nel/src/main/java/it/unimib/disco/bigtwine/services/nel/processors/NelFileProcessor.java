package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.commons.processors.file.FileProcessor;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParser;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducer;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public abstract class NelFileProcessor implements Processor, FileProcessor<RecognizedText> {

    private final Logger log = LoggerFactory.getLogger(NelFileProcessor.class);

    protected OutputParserBuilder outputParserBuilder;
    protected InputProducerBuilder inputProducerBuilder;
    protected String processorId;
    protected File workingDirectory;
    protected File inputDirectory;
    protected File outputDirectory;
    protected ProcessorListener<LinkedText> processorListener;

    public NelFileProcessor(InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        this.setInputProducerBuilder(inputProducerBuilder);
        this.setOutputParserBuilder(outputParserBuilder);
    }

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    public InputProducerBuilder getInputProducerBuilder() {
        return this.inputProducerBuilder;
    }

    public void setInputProducerBuilder(InputProducerBuilder producerBuilder) {
        this.inputProducerBuilder = producerBuilder
            .setLinker(this.getLinker());
    }

    public OutputParserBuilder getOutputParserBuilder() {
        return this.outputParserBuilder;
    }

    public void setOutputParserBuilder(OutputParserBuilder outputParserBuilder) {
        this.outputParserBuilder = outputParserBuilder
            .setLinker(this.getLinker());
    }

    @Override
    public File getInputDirectory() {
        return this.inputDirectory;
    }

    @Override
    public void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    @Override
    public File getWorkingDirectory() {
        return this.workingDirectory;
    }

    @Override
    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getProcessorId() {
        return this.processorId;
    }

    @Override
    public void setListener(ProcessorListener<LinkedText> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean process(String tag, RecognizedText tweet) {
        return this.process(tag, new RecognizedText[]{tweet});
    }

    @Override
    public boolean generateInputFile(File file, RecognizedText[] tweets) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile(file.getName(), ".tmp", file.getAbsoluteFile().getParentFile());
        } catch (IOException e) {
            return false;
        }
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(tmpFile);
        } catch (IOException e) {
            log.debug("Cannot generate file writer: {}", e.getMessage());
            return false;
        }

        InputProducer inputProducer = this.inputProducerBuilder
            .setLinker(this.getLinker())
            .setWriter(fileWriter)
            .build();

        if (inputProducer == null) {
            return false;
        }

        try {
            inputProducer.append(tweets);
            inputProducer.close();
        } catch (IOException e) {
            log.debug("Cannot append tweets to input producer: {}", e.getMessage());
            return false;
        }

        try {
            Files.move(tmpFile.toPath(), file.toPath());
        } catch (IOException | SecurityException e) {
            log.debug("Cannot move generated input file to destination: {}", e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void processOutputFile(File outputFile) {
        OutputParser outputParser = this.outputParserBuilder
            .setLinker(this.getLinker())
            .setInput(outputFile)
            .build();

        if (outputParser == null) {
            return;
        }

        String tag = FilenameUtils.removeExtension(outputFile.getName());
        LinkedText[] tweets = outputParser.items();

        if (!tag.isEmpty() && this.processorListener != null && tweets != null) {
            this.processorListener.onProcessed(this, tag, tweets);
        }
    }
}
