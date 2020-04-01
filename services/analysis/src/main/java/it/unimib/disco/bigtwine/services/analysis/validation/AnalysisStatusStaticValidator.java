package it.unimib.disco.bigtwine.services.analysis.validation;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;

import java.util.*;

public class AnalysisStatusStaticValidator implements AnalysisStatusValidator {

    private final Map<AnalysisStatus, Set<AnalysisStatus>> validStatusMap;

    public AnalysisStatusStaticValidator() {
        this.validStatusMap = new HashMap<>();

        this.validStatusMap.put(
            AnalysisStatus.READY,
            new HashSet<>(Arrays.asList(
                AnalysisStatus.STARTED,
                AnalysisStatus.FAILED,
                AnalysisStatus.CANCELLED))
        );

        this.validStatusMap.put(
            AnalysisStatus.STARTED,
            new HashSet<>(Arrays.asList(
                AnalysisStatus.STOPPED,
                AnalysisStatus.COMPLETED,
                AnalysisStatus.FAILED,
                AnalysisStatus.CANCELLED))
        );

        this.validStatusMap.put(
            AnalysisStatus.STOPPED,
            new HashSet<>(Arrays.asList(
                AnalysisStatus.STARTED,
                AnalysisStatus.FAILED,
                AnalysisStatus.CANCELLED))
        );

        this.validStatusMap.put(
            AnalysisStatus.COMPLETED,
            new HashSet<>(Collections.singletonList(
                AnalysisStatus.CANCELLED))
        );

        this.validStatusMap.put(
            AnalysisStatus.FAILED,
            new HashSet<>(Collections.singletonList(
                AnalysisStatus.CANCELLED))
        );

        this.validStatusMap.put(
            AnalysisStatus.CANCELLED,
            new HashSet<>()
        );
    }

    @Override
    public boolean validate(AnalysisStatus from, AnalysisStatus to) {
        boolean changed = from != to;

        if (!changed) {
            return true;
        }

        return this.validStatusMap
            .get(from)
            .contains(to);
    }
}
