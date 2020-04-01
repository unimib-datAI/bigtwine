package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import java.util.HashMap;
import java.util.Map;

public class AnalysisResultPayloadMapperLocator {
    private Map<Class, AnalysisResultPayloadMapper> mappers = new HashMap<>();

    public void registerMapper(Class payloadClass, AnalysisResultPayloadMapper mapper) {
        this.mappers.put(payloadClass, mapper);
    }

    public AnalysisResultPayloadMapper getMapper(Class payloadClass) {
        return this.mappers.get(payloadClass);
    }
}
