package it.unimib.disco.bigtwine.services.analysis.domain.enumeration;

/**
 * The AnalysisInputType enumeration.
 */
public enum AnalysisInputType {
    QUERY, DATASET, GEO_AREA;

    public static class Constants {
        public static final String QUERY_VALUE = "QUERY";
        public static final String DATASET_VALUE = "DATASET";
        public static final String GEO_AREA_VALUE = "GEO_AREA";
    }
}
