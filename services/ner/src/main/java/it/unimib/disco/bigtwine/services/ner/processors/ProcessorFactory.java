package it.unimib.disco.bigtwine.services.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.PerpetualFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.services.ner.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.ner.Recognizer;
import it.unimib.disco.bigtwine.services.ner.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.services.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.ner.producers.InputProducerBuilder;
import org.springframework.beans.factory.FactoryBean;

import java.io.File;
import java.nio.file.Files;

public class ProcessorFactory implements FactoryBean<NerProcessor> {

    private Recognizer recognizer;
    private ExecutorFactory executorFactory;
    private ApplicationProperties.Processors processorsProps;

    public ProcessorFactory(ApplicationProperties.Processors processorsProps, ExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
        this.processorsProps = processorsProps;
    }

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    public Recognizer getRecognizer() {
        return this.recognizer;
    }

    private NerProcessor getRitterProcessor() throws Exception {
        Executor executor = this.executorFactory.getExecutor(recognizer);

        if (!(executor instanceof PerpetualFileExecutor))
            throw new RuntimeException("Invalid configuration: Ritter processor requires an PerpetualFileExecutor.");

        RitterProcessor processor = new RitterProcessor(
            (PerpetualFileExecutor)executor,
            InputProducerBuilder.getDefaultBuilder(),
            OutputParserBuilder.getDefaultBuilder());

        final String suffixFilter = this.processorsProps.getRitter().getFileMonitorSuffixFilter();
        final String suffixExclusion = this.processorsProps.getRitter().getFileMonitorSuffixExclusion();
        final boolean useTmpWD = this.processorsProps.getRitter().getUseTmpWorkingDirectory();
        final String wds = this.processorsProps.getRitter().getWorkingDirectory();

        File wd;
        if (useTmpWD || wds == null) {
            wd = Files.createTempDirectory("ner").toFile();
        }else {
            wd = new File(wds);
        }

        processor.setWorkingDirectory(wd);
        processor.setMonitorFilesOnly(true);
        processor.setMonitorSuffixFilter(suffixFilter);
        processor.setMonitorSuffixExclusion(suffixExclusion);

        return processor;
    }

    private NerProcessor getTestProcessor() throws Exception {
        return new TestProcessor();
    }

    public NerProcessor getProcessor() throws Exception {
        if (this.recognizer == null) {
            throw new IllegalArgumentException("recognizer not set");
        }

        switch (recognizer) {
            case ritter:
                return this.getRitterProcessor();
            case test:
                return this.getTestProcessor();
            default:
                return null;
        }
    }

    public NerProcessor getProcessor(Recognizer recognizer) throws Exception {
        this.setRecognizer(recognizer);
        return this.getProcessor();
    }

    public NerProcessor getDefaultProcessor() throws Exception {
        return this.getProcessor(Recognizer.getDefault());
    }

    @Override
    public NerProcessor getObject() throws Exception {
        return this.getProcessor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.recognizer == null) {
            return null;
        }

        switch (recognizer) {
            case ritter:
                return RitterProcessor.class;
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
