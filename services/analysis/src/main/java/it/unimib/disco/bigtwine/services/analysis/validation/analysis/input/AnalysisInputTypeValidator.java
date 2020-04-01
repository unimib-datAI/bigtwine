package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnalysisInputTypeValidator {
    private Map<AnalysisType, Set<AnalysisInputType>> supportedTypes = new HashMap<>();

    public void registerSupportedTypes(AnalysisType analysisType, Set<AnalysisInputType> types) {
        this.supportedTypes.put(analysisType, types);
    }

    public void validate(AnalysisType analysisType, AnalysisInputType type) {
        if (!this.supportedTypes.containsKey(analysisType)) {
            throw new RuntimeException("Unregistered analysis type: " + analysisType);
        }

        if (!this.supportedTypes.get(analysisType).contains(type)) {
            throw new InvalidAnalysisInputProvidedException(
                String.format("Analysis of type '%s' does not support input of type '%s'", analysisType, type)
            );
        }
    }
}
