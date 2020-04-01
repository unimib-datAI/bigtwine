package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.executors.PerpetualExecutor;
import it.unimib.disco.bigtwine.commons.executors.PerpetualFileExecutor;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.file.PerpetualFileProcessor;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.nio.file.Paths;

public abstract class NelPerpetualFileProcessor extends NelFileProcessor implements PerpetualFileProcessor<RecognizedText> {
    protected PerpetualFileExecutor executor;
    protected FileAlterationMonitor fileMonitor;
    protected boolean monitorFilesOnly;
    protected String monitorSuffixFilter;
    protected String monitorSuffixExclusion;

    public NelPerpetualFileProcessor(InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder, PerpetualFileExecutor executor) {
        super(inputProducerBuilder, outputParserBuilder);
        this.executor = executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof PerpetualFileExecutor)) {
            throw new IllegalArgumentException("Unsupported executor type");
        }
        this.executor = (PerpetualFileExecutor)executor;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    public void setPerpetualFileExecutor(PerpetualFileExecutor executor) {
        this.executor = executor;
    }

    @Override
    public PerpetualFileExecutor getPerpetualFileExecutor() {
        return this.executor;
    }

    @Override
    public PerpetualExecutor getPerpetualExecutor() {
        return this.executor;
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
    public String getMonitorSuffixExclusion() {
        return monitorSuffixExclusion;
    }

    @Override
    public void setMonitorSuffixExclusion(String suffixFilter) {
        this.monitorSuffixExclusion = suffixFilter;
    }

    @Override
    public boolean configureProcessor() {
        this.processorId = RandomStringUtils.randomAlphanumeric(16);
        this.inputDirectory = Paths.get(this.getWorkingDirectory().toString(), "input").toFile();
        this.outputDirectory = Paths.get(this.getWorkingDirectory().toString(), "output").toFile();

        if (!this.setupWorkingDirectory()) {
            return false;
        }

        this.getPerpetualFileExecutor().setInputWorkingDirectory(this.inputDirectory);
        this.getPerpetualFileExecutor().setOutputWorkingDirectory(this.outputDirectory);
        this.getPerpetualExecutor().run();

        if (!this.configureFileMonitor()) {
            return false;
        }

        return this.startFileMonitor();
    }

    @Override
    public boolean process(String tag, RecognizedText[] tweets) {
        File inputFile = this.makeInputFile(tag);
        return this.generateInputFile(inputFile, tweets);
    }
}
