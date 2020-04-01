package it.unimib.disco.bigtwine.services.analysis.validation;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnalysisExportFormatValidator {
    private Map<AnalysisType, Set<String>> supportedFormats = new HashMap<>();

    public void registerSupportedFormats(AnalysisType analysisType, Set<String> formats) {
        this.supportedFormats.put(analysisType, formats);
    }

    public boolean validate(AnalysisType analysisType, String format) {
        if (!this.supportedFormats.containsKey(analysisType)) {
            throw new RuntimeException("Unregistered analysis type: " + analysisType);
        }

        return this.supportedFormats.get(analysisType).contains(format);
    }
}
