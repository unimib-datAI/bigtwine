package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;


import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;

public interface AnalysisInputValidatorLocator {
    AnalysisInputValidator getValidator(AnalysisInputType inputType);
}
