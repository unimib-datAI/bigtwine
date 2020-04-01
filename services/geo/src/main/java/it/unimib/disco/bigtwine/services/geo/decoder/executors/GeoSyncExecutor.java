package it.unimib.disco.bigtwine.services.geo.decoder.executors;

import it.unimib.disco.bigtwine.commons.executors.SyncExecutor;
import it.unimib.disco.bigtwine.services.geo.domain.DecodedLocation;
import it.unimib.disco.bigtwine.services.geo.domain.Location;

public interface GeoSyncExecutor extends SyncExecutor {
    DecodedLocation search(Location location);
    DecodedLocation[] search(Location[] locations);
}
