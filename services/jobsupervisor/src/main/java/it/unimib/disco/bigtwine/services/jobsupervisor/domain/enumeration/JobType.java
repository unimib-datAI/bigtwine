package it.unimib.disco.bigtwine.services.jobsupervisor.domain.enumeration;

public enum JobType {
    PROCESSING, EXPORT;

    public static JobType DEFAULT = JobType.PROCESSING;
}
