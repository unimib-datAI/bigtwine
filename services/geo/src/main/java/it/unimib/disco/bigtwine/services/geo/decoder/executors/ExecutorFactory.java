package it.unimib.disco.bigtwine.services.geo.decoder.executors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.services.geo.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.geo.decoder.Decoder;
import org.springframework.beans.factory.FactoryBean;

public class ExecutorFactory implements FactoryBean<Executor> {

    private Decoder decoder;
    private ApplicationProperties.Executors executorsProps;

    public ExecutorFactory(ApplicationProperties.Executors executorsProps) {
        this.executorsProps = executorsProps;
    }


    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public Decoder getDecoder() {
        return this.decoder;
    }

    public Executor getExecutor(Decoder decoder) throws Exception {
        this.setDecoder(decoder);
        return this.getExecutor();
    }

    public Executor getExecutor() throws Exception {
        if (this.decoder == null) {
            throw new IllegalArgumentException("decoder not set");
        }

        switch (decoder) {
            case nominatim:
                NominatimSyncExecutor executor = new NominatimSyncExecutor();
                executor.setApiEmail(executorsProps.getNominatim().getApiEmail());

                return executor;
            default:
                return null;
        }
    }

    public Executor getDefaultExecutor() throws Exception {
        return this.getExecutor(Decoder.getDefault());
    }

    @Override
    public Executor getObject() throws Exception {
        return this.getExecutor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.decoder == null) {
            return null;
        }

        switch (decoder) {
            case nominatim:
                return NominatimSyncExecutor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
