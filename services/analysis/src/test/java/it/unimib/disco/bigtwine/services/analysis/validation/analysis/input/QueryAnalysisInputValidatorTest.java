package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.DatasetAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class QueryAnalysisInputValidatorTest {

    private QueryAnalysisInputValidator validator;

    private static final List<String> VALID_TOKENS = Arrays.asList("query", "di", "prova");
    private static final List<String> EMPTY_TOKENS = Arrays.asList("", "  ", " ");

    @Before
    public void setUp() {
        this.validator = new QueryAnalysisInputValidator();
    }

    @Test
    public void testValidInput() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(VALID_TOKENS)
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testEmptyTokens() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(EMPTY_TOKENS)
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testNullTokens() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(null)
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    @SuppressWarnings("unchecked")
    public void testNoTokens() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(Collections.EMPTY_LIST)
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testNullJoinOperator() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(VALID_TOKENS)
            .joinOperator(null);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testInvalidInputTypeProvided() {
        AnalysisInput input = new DatasetAnalysisInput()
            .documentId("aaaa");

        this.validator.validate(input);
    }
}
