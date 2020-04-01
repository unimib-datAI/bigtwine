package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnalysisInputValidatorRegistryTest {

    private AnalysisInputValidatorRegistry registry;

    @Before
    public void setUp() {
        this.registry = new AnalysisInputValidatorRegistry();
    }

    @Test
    public void testValidatorRegistration() {
        AnalysisInputValidator validator = new QueryAnalysisInputValidator();
        this.registry.registerInputValidator(validator, AnalysisInputType.QUERY);

        assertEquals(validator, this.registry.getValidator(AnalysisInputType.QUERY));
        assertNull(this.registry.getValidator(AnalysisInputType.DATASET));
    }

    @Test
    public void testValidatorOverwrite() {
        AnalysisInputValidator validator1 = new QueryAnalysisInputValidator();
        AnalysisInputValidator validator2 = new QueryAnalysisInputValidator();

        this.registry.registerInputValidator(validator1, AnalysisInputType.QUERY);

        assertEquals(validator1, this.registry.getValidator(AnalysisInputType.QUERY));

        this.registry.registerInputValidator(validator2, AnalysisInputType.QUERY);

        assertEquals(validator2, this.registry.getValidator(AnalysisInputType.QUERY));
    }
}
