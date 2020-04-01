package it.unimib.disco.bigtwine.services.analysis.validation;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;

public interface AnalysisStatusValidator {
    boolean validate(AnalysisStatus from, AnalysisStatus to);
}
