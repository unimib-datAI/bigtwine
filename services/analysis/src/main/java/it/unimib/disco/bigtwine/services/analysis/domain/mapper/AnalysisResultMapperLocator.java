package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import java.util.HashMap;
import java.util.Map;

public class AnalysisResultMapperLocator {
    private Map<Class, AnalysisResultMapper> mappers = new HashMap<>();

    public void registerMapper(Class payloadClass, AnalysisResultMapper mapper) {
        this.mappers.put(payloadClass, mapper);
    }

    public AnalysisResultMapper getMapper(Class payloadClass) {
        return this.mappers.get(payloadClass);
    }
}
