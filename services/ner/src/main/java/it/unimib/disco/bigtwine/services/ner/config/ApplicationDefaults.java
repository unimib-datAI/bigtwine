package it.unimib.disco.bigtwine.services.ner.config;

public interface ApplicationDefaults {
    String defaultRecognizer = "ritter";

    interface Executors {
        interface RitterDocker {
            boolean classify = true;
        }
    }

    interface Processors {
        interface Ritter {
            String workingDirectory = "/tmp/ner";
            boolean useTmpWorkingDirectory = false;
            String fileMonitorSuffixFilter = null;
            String fileMonitorSuffixExclusion = ".tmp";
        }
    }
}
