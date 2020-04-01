package it.unimib.disco.bigtwine.services.analysis.domain.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * The AnalysisErrorCode enumeration.
 */
public enum AnalysisErrorCode {
    GENERIC(1);

    private int value;
    private static Map<Integer, AnalysisErrorCode> map = new HashMap<>();

    AnalysisErrorCode(int value) {
        this.value = value;
    }

    static {
        for (AnalysisErrorCode errorCode : AnalysisErrorCode.values()) {
            map.put(errorCode.value, errorCode);
        }
    }

    public static AnalysisErrorCode valueOf(int errorCode) {
        return map.get(errorCode);
    }

    public int getValue() {
        return value;
    }
}
