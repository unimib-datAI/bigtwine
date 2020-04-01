package it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker;

public interface DockerJobExecutorConfig {
    String getNetworkId();
    String getImageName();
    boolean getAutoremoveContainer();
}
