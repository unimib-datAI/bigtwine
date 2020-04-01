package it.unimib.disco.bigtwine.services.geo.decoder.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.executors.SyncExecutor;
import it.unimib.disco.bigtwine.services.geo.domain.DecodedLocation;
import it.unimib.disco.bigtwine.services.geo.domain.Location;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.commons.processors.SyncProcessor;
import it.unimib.disco.bigtwine.services.geo.decoder.executors.GeoSyncExecutor;

public abstract class GeoSyncProcessor implements Processor, SyncProcessor {

    protected GeoSyncExecutor executor;
    protected ProcessorListener<DecodedLocation> listener;

    public GeoSyncProcessor(GeoSyncExecutor executor) {
        super();
        this.setExecutor(executor);
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof GeoSyncExecutor)) {
            throw new IllegalArgumentException("Invalid executor type, GeoSyncExecutor needed");
        }

        this.executor = (GeoSyncExecutor)executor;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public SyncExecutor getSyncExecutor() {
        return this.executor;
    }

    public GeoSyncExecutor getGeoSyncExecutor() {
        return this.executor;
    }

    @Override
    public void setListener(ProcessorListener<DecodedLocation> listener) {
        this.listener = listener;
    }

    @Override
    public boolean configureProcessor() {
        return true;
    }

    @Override
    public boolean process(String tag, Location item) {
        return this.process(tag, new Location[]{item});
    }

    @Override
    public boolean process(String tag, Location[] items) {
        DecodedLocation[] addresses = this.getGeoSyncExecutor().search(items);
        if (this.listener != null && addresses != null) {
            this.listener.onProcessed(this, tag, addresses);
        }

        return addresses != null;
    }
}
