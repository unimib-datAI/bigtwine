package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.DatasetAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;


public class DatasetAnalysisInputValidatorTest {

    private static final String VALID_DOCUMENT_ID = "aaaaa";
    private static final String INVALID_DOCUMENT_ID = " ";

    private DatasetAnalysisInputValidator validator = new DatasetAnalysisInputValidator();

    @Before
    public void setUp() {
        this.validator = new DatasetAnalysisInputValidator();
    }

    @Test
    public void testValidInput() {
        AnalysisInput input = new DatasetAnalysisInput()
            .documentId(VALID_DOCUMENT_ID);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testNullInput() {
        AnalysisInput input = new DatasetAnalysisInput()
            .documentId(null);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testEmptyInput() {
        AnalysisInput input = new DatasetAnalysisInput()
            .documentId(INVALID_DOCUMENT_ID);

        this.validator.validate(input);
    }

    @Test(expected = InvalidAnalysisInputProvidedException.class)
    public void testInvalidInputType() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(Arrays.asList("query", "di", "prova"))
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY);

        this.validator.validate(input);
    }

}
