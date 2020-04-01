package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.NopExecutor;
import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;
import it.unimib.disco.bigtwine.services.nel.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.nel.Linker;
import it.unimib.disco.bigtwine.services.nel.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;
import org.springframework.beans.factory.FactoryBean;

import java.io.File;
import java.nio.file.Files;


public class ProcessorFactory implements FactoryBean<Processor> {

    private Linker linker;
    private ExecutorFactory executorFactory;
    private ApplicationProperties.Processors processorsProps;

    public ProcessorFactory(ApplicationProperties.Processors processorsProps, ExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
        this.processorsProps = processorsProps;
    }

    public void setLinker(Linker linker) {
        this.linker = linker;
    }

    public Linker getLinker() {
        return this.linker;
    }

    protected Processor getMind2016SyncProcessor() throws Exception {
        return new Mind2016SyncProcessor(
            (SyncFileExecutor) this.executorFactory.getExecutor(this.linker),
            InputProducerBuilder.getDefaultBuilder(),
            OutputParserBuilder.getDefaultBuilder());
    }

    protected Processor getMind2016PerpetualProcessor() throws Exception {
        Mind2016PerpetualProcessor processor = new Mind2016PerpetualProcessor(
            InputProducerBuilder.getDefaultBuilder(),
            OutputParserBuilder.getDefaultBuilder(),
            new NopExecutor());

        final String suffixFilter = this.processorsProps.getMind2016().getFileMonitorSuffixFilter();
        final String suffixExclusion = this.processorsProps.getMind2016().getFileMonitorSuffixFilter();
        final boolean useTmpWD = this.processorsProps.getMind2016().isUseTmpWorkingDirectory();
        final String wds = this.processorsProps.getMind2016().getWorkingDirectory();

        File wd;
        if (useTmpWD || wds == null) {
            wd = Files.createTempDirectory("nel").toFile();
        }else {
            wd = new File(wds);
        }

        processor.setWorkingDirectory(wd);
        processor.setMonitorSuffixFilter(suffixFilter);
        processor.setMonitorSuffixExclusion(suffixExclusion);
        processor.setMonitorFilesOnly(true);

        return processor;
    }

    protected Processor getTestProcessor() throws Exception {
        return new TestProcessor();
    }

    public Processor getProcessor() throws Exception {
        if (this.linker == null) {
            throw new IllegalArgumentException("linker not set");
        }

        switch (linker) {
            case mind2016:
                return this.getMind2016PerpetualProcessor();
            case test:
                return this.getTestProcessor();
            default:
                return null;
        }
    }

    public Processor getProcessor(Linker linker) throws Exception {
        this.setLinker(linker);
        return this.getProcessor();
    }

    public Processor getDefaultProcessor() throws Exception {
        return this.getProcessor(Linker.getDefault());
    }

    @Override
    public Processor getObject() throws Exception {
        return this.getProcessor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.linker == null) {
            return null;
        }

        switch (linker) {
            case mind2016:
                return Mind2016SyncProcessor.class;
            case test:
                return TestProcessor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
