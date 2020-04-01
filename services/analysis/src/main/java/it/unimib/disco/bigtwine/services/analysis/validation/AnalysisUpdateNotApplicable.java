package it.unimib.disco.bigtwine.services.analysis.validation;

import javax.validation.ValidationException;

public class AnalysisUpdateNotApplicable extends ValidationException {
    public AnalysisUpdateNotApplicable(String message) {
        super(message);
    }
}
