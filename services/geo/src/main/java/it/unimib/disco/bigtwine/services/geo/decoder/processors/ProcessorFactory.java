package it.unimib.disco.bigtwine.services.geo.decoder.processors;

import it.unimib.disco.bigtwine.services.geo.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.geo.decoder.Decoder;
import it.unimib.disco.bigtwine.services.geo.decoder.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.services.geo.decoder.executors.GeoSyncExecutor;
import org.springframework.beans.factory.FactoryBean;

public class ProcessorFactory implements FactoryBean<Processor> {

    private Decoder decoder;
    private ExecutorFactory executorFactory;
    private ApplicationProperties.Processors processorsProps;


    public ProcessorFactory(ApplicationProperties.Processors processorsProps, ExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
        this.processorsProps = processorsProps;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public Decoder getDecoder() {
        return this.decoder;
    }


    protected Processor getNominatimProcessor() throws Exception {
        return new NominatimProcessor((GeoSyncExecutor) this.executorFactory.getExecutor(this.decoder));
    }

    public Processor getProcessor() throws Exception {
        if (this.decoder == null) {
            throw new IllegalArgumentException("decoder not set");
        }

        switch (decoder) {
            case nominatim:
                return this.getNominatimProcessor();
            default:
                return null;
        }
    }

    public Processor getProcessor(Decoder decoder) throws Exception {
        this.setDecoder(decoder);
        return this.getProcessor();
    }

    public Processor getDefaultProcessor() throws Exception {
        return this.getProcessor(Decoder.getDefault());
    }

    @Override
    public Processor getObject() throws Exception {
        return this.getProcessor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.decoder == null) {
            return null;
        }

        switch (decoder) {
            case nominatim:
                return NominatimProcessor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
