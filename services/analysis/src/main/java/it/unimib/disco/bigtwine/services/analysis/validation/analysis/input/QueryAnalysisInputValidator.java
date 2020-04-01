package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;

import java.util.List;

public class QueryAnalysisInputValidator implements AnalysisInputValidator {
    @Override
    public void validate(AnalysisInput input) throws InvalidAnalysisInputProvidedException {
        if (!(input instanceof QueryAnalysisInput)) {
            throw new InvalidAnalysisInputProvidedException("Query input not provided");
        }

        QueryAnalysisInput queryInput = (QueryAnalysisInput)input;
        List<String> tokens = queryInput.getTokens();

        if (tokens == null || tokens.isEmpty()) {
            throw new InvalidAnalysisInputProvidedException("Invalid query input, no tokens provided");
        }

        int emptyTokens = 0;
        for (String token: tokens) {
            if (token.isEmpty() || token.trim().isEmpty()) {
                emptyTokens++;
            }
        }

        if (emptyTokens == tokens.size()) {
            throw new InvalidAnalysisInputProvidedException("Invalid query input, only empty tokens provided");
        }

        if (queryInput.getJoinOperator() == null) {
            throw new InvalidAnalysisInputProvidedException("Invalid query input, no join operator provided");
        }
    }
}
