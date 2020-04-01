package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;

import java.util.HashMap;
import java.util.Map;

public class AnalysisInputValidatorRegistry implements AnalysisInputValidatorLocator {
    private Map<AnalysisInputType, AnalysisInputValidator> validators = new HashMap<>();

    public void registerInputValidator(AnalysisInputValidator validator, AnalysisInputType inputType) {
        this.validators.put(inputType, validator);
    }

    @Override
    public AnalysisInputValidator getValidator(AnalysisInputType inputType) {
        return this.validators.get(inputType);
    }
}
