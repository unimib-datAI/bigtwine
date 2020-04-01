package it.unimib.disco.bigtwine.services.analysis.validation;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;

import javax.validation.ValidationException;

public class InvalidAnalysisStatusException extends ValidationException {
    public InvalidAnalysisStatusException(AnalysisStatus oldStatus, AnalysisStatus newStatus) {
        super("The status of the analysis cannot be changed from '" + oldStatus + "' to '" + newStatus + "'");
    }
}
