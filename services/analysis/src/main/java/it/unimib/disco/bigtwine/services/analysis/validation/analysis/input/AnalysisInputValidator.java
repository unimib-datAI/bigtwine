package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;

@FunctionalInterface
public interface AnalysisInputValidator {
    void validate(AnalysisInput input) throws InvalidAnalysisInputProvidedException;
}
