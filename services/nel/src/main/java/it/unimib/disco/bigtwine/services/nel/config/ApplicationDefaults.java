package it.unimib.disco.bigtwine.services.nel.config;

public interface ApplicationDefaults {
    String defaultLinker = "mind2016";

    interface Processors {
        interface Mind2016 {
            String workingDirectory = null;
            boolean useTmpWorkingDirectory = false;
            String fileMonitorSuffixFilter = null;
            String fileMonitorSuffixExclusion = ".tmp";
        }
    }
}
