package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.DatasetAnalysisInput;

public class DatasetAnalysisInputValidator implements AnalysisInputValidator {
    @Override
    public void validate(AnalysisInput input) throws InvalidAnalysisInputProvidedException {
        if (!(input instanceof DatasetAnalysisInput)) {
            throw new InvalidAnalysisInputProvidedException("Dataset input not provided");
        }

        String documentId = ((DatasetAnalysisInput)input).getDocumentId();

        if (documentId == null || documentId.trim().isEmpty()) {
            throw new InvalidAnalysisInputProvidedException("Invalid document input, no document id provided");
        }
    }
}
