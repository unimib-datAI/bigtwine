package it.unimib.disco.bigtwine.services.analysis.validation;

import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnalysisStatusStaticValidatorTest {

    private final AnalysisStatusValidator validator;

    public AnalysisStatusStaticValidatorTest() {
        validator = new AnalysisStatusStaticValidator();
    }

    @Test
    public void testSelfPaths() {
        assertTrue(validator.validate(AnalysisStatus.READY, AnalysisStatus.READY));
        assertTrue(validator.validate(AnalysisStatus.STARTED, AnalysisStatus.STARTED));
        assertTrue(validator.validate(AnalysisStatus.STOPPED, AnalysisStatus.STOPPED));
        assertTrue(validator.validate(AnalysisStatus.COMPLETED, AnalysisStatus.COMPLETED));
        assertTrue(validator.validate(AnalysisStatus.FAILED, AnalysisStatus.FAILED));
    }

    @Test
    public void testValidPaths() {
        assertTrue(validator.validate(AnalysisStatus.READY, AnalysisStatus.STARTED));
        assertTrue(validator.validate(AnalysisStatus.READY, AnalysisStatus.FAILED));
        assertTrue(validator.validate(AnalysisStatus.READY, AnalysisStatus.CANCELLED));

        assertTrue(validator.validate(AnalysisStatus.STARTED, AnalysisStatus.STOPPED));
        assertTrue(validator.validate(AnalysisStatus.STARTED, AnalysisStatus.COMPLETED));
        assertTrue(validator.validate(AnalysisStatus.STARTED, AnalysisStatus.FAILED));
        assertTrue(validator.validate(AnalysisStatus.STARTED, AnalysisStatus.CANCELLED));

        assertTrue(validator.validate(AnalysisStatus.STOPPED, AnalysisStatus.STARTED));
        assertTrue(validator.validate(AnalysisStatus.STOPPED, AnalysisStatus.FAILED));
        assertTrue(validator.validate(AnalysisStatus.STOPPED, AnalysisStatus.CANCELLED));

        assertTrue(validator.validate(AnalysisStatus.COMPLETED, AnalysisStatus.CANCELLED));

        assertTrue(validator.validate(AnalysisStatus.FAILED, AnalysisStatus.CANCELLED));
    }

    @Test
    public void testInvalidPaths() {
        assertFalse(validator.validate(AnalysisStatus.READY, AnalysisStatus.STOPPED));
        assertFalse(validator.validate(AnalysisStatus.READY, AnalysisStatus.COMPLETED));

        assertFalse(validator.validate(AnalysisStatus.STARTED, AnalysisStatus.READY));

        assertFalse(validator.validate(AnalysisStatus.STOPPED, AnalysisStatus.READY));
        assertFalse(validator.validate(AnalysisStatus.STOPPED, AnalysisStatus.COMPLETED));

        assertFalse(validator.validate(AnalysisStatus.COMPLETED, AnalysisStatus.READY));
        assertFalse(validator.validate(AnalysisStatus.COMPLETED, AnalysisStatus.STARTED));
        assertFalse(validator.validate(AnalysisStatus.COMPLETED, AnalysisStatus.STOPPED));
        assertFalse(validator.validate(AnalysisStatus.COMPLETED, AnalysisStatus.FAILED));

        assertFalse(validator.validate(AnalysisStatus.CANCELLED, AnalysisStatus.READY));
        assertFalse(validator.validate(AnalysisStatus.CANCELLED, AnalysisStatus.STARTED));
        assertFalse(validator.validate(AnalysisStatus.CANCELLED, AnalysisStatus.STOPPED));
        assertFalse(validator.validate(AnalysisStatus.CANCELLED, AnalysisStatus.FAILED));
        assertFalse(validator.validate(AnalysisStatus.CANCELLED, AnalysisStatus.COMPLETED));
    }
}
