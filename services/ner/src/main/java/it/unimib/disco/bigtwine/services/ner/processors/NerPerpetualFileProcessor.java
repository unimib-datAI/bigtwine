package it.unimib.disco.bigtwine.services.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.*;
import it.unimib.disco.bigtwine.commons.executors.PerpetualFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.PerpetualExecutor;
import it.unimib.disco.bigtwine.services.ner.domain.PlainText;
import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.commons.processors.file.PerpetualFileProcessor;
import it.unimib.disco.bigtwine.services.ner.parsers.OutputParser;
import it.unimib.disco.bigtwine.services.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.ner.producers.InputProducer;
import it.unimib.disco.bigtwine.services.ner.producers.InputProducerBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class NerPerpetualFileProcessor implements NerProcessor, PerpetualFileProcessor<PlainText> {

    private final Logger log = LoggerFactory.getLogger(NerPerpetualFileProcessor.class);

    protected PerpetualFileExecutor executor;
    protected OutputParserBuilder outputParserBuilder;
    protected InputProducerBuilder inputProducerBuilder;
    protected FileAlterationMonitor fileMonitor;
    protected String processorId;
    protected File workingDirectory;
    protected File inputDirectory;
    protected File outputDirectory;
    protected boolean monitorFilesOnly;
    protected String monitorSuffixFilter;
    protected ProcessorListener<RecognizedText> processorListener;

    public NerPerpetualFileProcessor(PerpetualFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        this.setExecutor(executor);
        this.setInputProducerBuilder(inputProducerBuilder);
        this.setOutputParserBuilder(outputParserBuilder);
    }

    @Override
    public FileAlterationMonitor getFileMonitor() {
        return fileMonitor;
    }

    @Override
    public void setFileMonitor(FileAlterationMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
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
            .setRecognizer(this.getRecognizer());
    }

    public OutputParserBuilder getOutputParserBuilder() {
        return this.outputParserBuilder;
    }

    public void setOutputParserBuilder(OutputParserBuilder outputParserBuilder) {
        this.outputParserBuilder = outputParserBuilder
            .setRecognizer(this.getRecognizer());
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof PerpetualFileExecutor)) {
            throw new IllegalArgumentException("Unsupported executor type");
        }
        this.executor = (PerpetualFileExecutor)executor;
    }

    @Override
    public PerpetualExecutor getPerpetualExecutor() {
        return this.executor;
    }

    @Override
    public PerpetualFileExecutor getPerpetualFileExecutor() {
        return this.executor;
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

    public boolean getMonitorFilesOnly() {
        return monitorFilesOnly;
    }

    @Override
    public void setMonitorFilesOnly(boolean monitorFilesOnly) {
        this.monitorFilesOnly = monitorFilesOnly;
    }

    @Override
    public String getMonitorSuffixFilter() {
        return monitorSuffixFilter;
    }

    @Override
    public void setMonitorSuffixFilter(String monitorSuffixFilter) {
        this.monitorSuffixFilter = monitorSuffixFilter;
    }

    @Override
    public void setListener(ProcessorListener<RecognizedText> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean configureProcessor() {
        this.processorId = RandomStringUtils.randomAlphanumeric(16);
        this.inputDirectory = Paths.get(this.getWorkingDirectory().toString(), "input").toFile();
        this.outputDirectory = Paths.get(this.getWorkingDirectory().toString(), "output").toFile();

        if (!this.setupWorkingDirectory()) {
            log.debug("Cannot setup working directory");
            return false;
        }

        this.getPerpetualFileExecutor().setInputWorkingDirectory(this.inputDirectory);
        this.getPerpetualFileExecutor().setOutputWorkingDirectory(this.outputDirectory);
        this.getPerpetualExecutor().run();

        if (!this.configureFileMonitor()) {
            log.debug("Cannot configure file monitor");
            return false;
        }

        return this.startFileMonitor();
    }

    @Override
    public boolean process(String tag, PlainText tweet) {
        return this.process(tag, new PlainText[]{tweet});
    }

    @Override
    public boolean process(String tag, PlainText[] tweets) {
        File inputFile = this.makeInputFile(tag);
        return this.generateInputFile(inputFile, tweets);
    }

    @Override
    public boolean generateInputFile(File file, PlainText[] tweets) {
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
            .setRecognizer(this.getRecognizer())
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
        log.debug("Output file ready to parse: {}", outputFile.toString());

        OutputParser outputParser = this.outputParserBuilder
            .setRecognizer(this.getRecognizer())
            .setInput(outputFile)
            .build();

        if (outputParser == null) {
            return;
        }

        String tag = FilenameUtils.removeExtension(outputFile.getName());
        RecognizedText[] tweets = outputParser.tweets();

        if (!tag.isEmpty() && this.processorListener != null && tweets != null) {
            log.debug("Processed {} tweets", tweets.length);
            this.processorListener.onProcessed(this, tag, tweets);
        }
    }
}
