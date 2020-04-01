package it.unimib.disco.bigtwine.services.ner.executors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.executors.NopExecutor;
import it.unimib.disco.bigtwine.services.ner.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.ner.Recognizer;
import org.springframework.beans.factory.FactoryBean;

public class ExecutorFactory implements FactoryBean<Executor> {

    private Recognizer recognizer;
    private ApplicationProperties.Executors executorsProps;

    public ExecutorFactory(ApplicationProperties.Executors executorsProps) {
        this.executorsProps = executorsProps;
    }

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    public Recognizer getRecognizer() {
        return this.recognizer;
    }

    public Executor getExecutor(Recognizer recognizer) throws Exception {
        this.setRecognizer(recognizer);
        return this.getExecutor();
    }

    public Executor getExecutor() throws Exception {
        if (this.recognizer == null) {
            throw new IllegalArgumentException("recognizer not set");
        }

        switch (recognizer) {
            case ritter:
                return new NopExecutor();
            default:
                return null;
        }
    }

    public Executor getDefaultExecutor() throws Exception {
        return this.getExecutor(Recognizer.getDefault());
    }

    @Override
    public Executor getObject() throws Exception {
        return this.getExecutor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.recognizer == null) {
            return null;
        }

        switch (recognizer) {
            case ritter:
                return RitterDockerExecutor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
