package it.unimib.disco.bigtwine.services.jobsupervisor.config;

public interface ApplicationDefaults {
    interface TwitterNeel {
        interface Stream {
            String defaultLang = "en";
            int sampling = -1;
            int heartbeat = 30;
            boolean skipRetweets = true;

            interface FlinkJob {
                String javaBin = "java";
                String jarName = "StreamProcessor.jar";
                String jarClass = "it.unimib.disco.bigtwine.streamprocessor.TwitterStreamJob";
                String kubernetesTemplate = "";
            }
        }

        interface Dataset {
            int heartbeat = 5;

            interface FlinkJob {
                String javaBin = "java";
                String jarName = "StreamProcessor.jar";
                String jarClass = "it.unimib.disco.bigtwine.streamprocessor.TwitterStreamJob";
                String kubernetesTemplate = "";
            }
        }

        interface Export {
            int heartbeat = 2;

            interface FlinkJob {
                String javaBin = "java";
                String jarName = "StreamProcessor.jar";
                String jarClass = "it.unimib.disco.bigtwine.streamprocessor.ExportResultsJob";
                String kubernetesTemplate = "";
            }
        }
    }

    interface Kubernetes {
        String namespace = "";
    }

    interface Docker {
        String imageName = "bigtwine/flink-streamprocessor";
        String networkId = "bigtwine_default";
        String dockerHost = "tcp://host.docker.internal:9075";
        boolean autoremoveContainer = true;
    }
}
