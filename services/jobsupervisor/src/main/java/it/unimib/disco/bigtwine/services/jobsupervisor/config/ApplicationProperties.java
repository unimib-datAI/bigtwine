package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker.DockerJobExecutorConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Jobsupervisor.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private TwitterNeel twitterNeel = new TwitterNeel();
    private Kubernetes kubernetes = new Kubernetes();
    private Docker docker = new Docker();

    public TwitterNeel getTwitterNeel() {
        return twitterNeel;
    }

    public Kubernetes getKubernetes() {
        return kubernetes;
    }

    public Docker getDocker() {
        return docker;
    }

    public static class TwitterNeel {
        private Stream stream = new Stream();
        private Dataset dataset = new Dataset();
        private Export export = new Export();

        public Stream getStream() {
            return stream;
        }

        public Dataset getDataset() {
            return dataset;
        }

        public Export getExport() {
            return export;
        }

        public static class Stream {
            private String defaultLang = ApplicationDefaults.TwitterNeel.Stream.defaultLang;
            private int sampling = ApplicationDefaults.TwitterNeel.Stream.sampling;
            private int heartbeat = ApplicationDefaults.TwitterNeel.Stream.heartbeat;
            private boolean skipRetweets = ApplicationDefaults.TwitterNeel.Stream.skipRetweets;
            private FlinkJob flinkJob = new FlinkJob();

            public String getDefaultLang() {
                return defaultLang;
            }

            public void setDefaultLang(String defaultLang) {
                this.defaultLang = defaultLang;
            }

            public int getSampling() {
                return sampling;
            }

            public void setSampling(int sampling) {
                this.sampling = sampling;
            }

            public int getHeartbeat() {
                return heartbeat;
            }

            public void setHeartbeat(int heartbeat) {
                this.heartbeat = heartbeat;
            }

            public boolean isSkipRetweets() {
                return skipRetweets;
            }

            public void setSkipRetweets(boolean skipRetweets) {
                this.skipRetweets = skipRetweets;
            }

            public FlinkJob getFlinkJob() {
                return flinkJob;
            }

            public static class FlinkJob {
                private String javaBin = ApplicationDefaults.TwitterNeel.Stream.FlinkJob.javaBin;
                private String jarName = ApplicationDefaults.TwitterNeel.Stream.FlinkJob.jarName;
                private String jarClass = ApplicationDefaults.TwitterNeel.Stream.FlinkJob.jarClass;
                private String kubernetesTemplate = ApplicationDefaults.TwitterNeel.Stream.FlinkJob.kubernetesTemplate;

                public String getJavaBin() {
                    return javaBin;
                }

                public void setJavaBin(String javaBin) {
                    this.javaBin = javaBin;
                }

                public String getJarName() {
                    return jarName;
                }

                public void setJarName(String jarName) {
                    this.jarName = jarName;
                }

                public String getJarClass() {
                    return jarClass;
                }

                public void setJarClass(String jarClass) {
                    this.jarClass = jarClass;
                }

                public String getKubernetesTemplate() {
                    return kubernetesTemplate;
                }

                public void setKubernetesTemplate(String kubernetesTemplate) {
                    this.kubernetesTemplate = kubernetesTemplate;
                }
            }
        }

        public static class Dataset {
            private int heartbeat = ApplicationDefaults.TwitterNeel.Dataset.heartbeat;
            private FlinkJob flinkJob = new FlinkJob();

            public int getHeartbeat() {
                return heartbeat;
            }

            public void setHeartbeat(int heartbeat) {
                this.heartbeat = heartbeat;
            }

            public FlinkJob getFlinkJob() {
                return flinkJob;
            }

            public static class FlinkJob {
                private String javaBin = ApplicationDefaults.TwitterNeel.Dataset.FlinkJob.javaBin;
                private String jarName = ApplicationDefaults.TwitterNeel.Dataset.FlinkJob.jarName;
                private String jarClass = ApplicationDefaults.TwitterNeel.Dataset.FlinkJob.jarClass;
                private String kubernetesTemplate = ApplicationDefaults.TwitterNeel.Dataset.FlinkJob.kubernetesTemplate;

                public String getJavaBin() {
                    return javaBin;
                }

                public void setJavaBin(String javaBin) {
                    this.javaBin = javaBin;
                }

                public String getJarName() {
                    return jarName;
                }

                public void setJarName(String jarName) {
                    this.jarName = jarName;
                }

                public String getJarClass() {
                    return jarClass;
                }

                public void setJarClass(String jarClass) {
                    this.jarClass = jarClass;
                }

                public String getKubernetesTemplate() {
                    return kubernetesTemplate;
                }

                public void setKubernetesTemplate(String kubernetesTemplate) {
                    this.kubernetesTemplate = kubernetesTemplate;
                }
            }
        }

        public static class Export {
            private int heartbeat = ApplicationDefaults.TwitterNeel.Export.heartbeat;
            private FlinkJob flinkJob = new FlinkJob();

            public int getHeartbeat() {
                return heartbeat;
            }

            public void setHeartbeat(int heartbeat) {
                this.heartbeat = heartbeat;
            }

            public FlinkJob getFlinkJob() {
                return flinkJob;
            }

            public static class FlinkJob {
                private String javaBin = ApplicationDefaults.TwitterNeel.Export.FlinkJob.javaBin;
                private String jarName = ApplicationDefaults.TwitterNeel.Export.FlinkJob.jarName;
                private String jarClass = ApplicationDefaults.TwitterNeel.Export.FlinkJob.jarClass;
                private String kubernetesTemplate = ApplicationDefaults.TwitterNeel.Export.FlinkJob.kubernetesTemplate;

                public String getJavaBin() {
                    return javaBin;
                }

                public void setJavaBin(String javaBin) {
                    this.javaBin = javaBin;
                }

                public String getJarName() {
                    return jarName;
                }

                public void setJarName(String jarName) {
                    this.jarName = jarName;
                }

                public String getJarClass() {
                    return jarClass;
                }

                public void setJarClass(String jarClass) {
                    this.jarClass = jarClass;
                }

                public String getKubernetesTemplate() {
                    return kubernetesTemplate;
                }

                public void setKubernetesTemplate(String kubernetesTemplate) {
                    this.kubernetesTemplate = kubernetesTemplate;
                }
            }
        }
    }

    public static class Kubernetes {
        private String namespace = ApplicationDefaults.Kubernetes.namespace;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
    }

    public static class Docker implements DockerJobExecutorConfig {
        private String networkId = ApplicationDefaults.Docker.networkId;
        private String imageName = ApplicationDefaults.Docker.imageName;
        private String dockerHost = ApplicationDefaults.Docker.dockerHost;
        private boolean autoremoveContainer = ApplicationDefaults.Docker.autoremoveContainer;

        @Override
        public String getNetworkId() {
            return networkId;
        }

        public void setNetworkId(String networkId) {
            this.networkId = networkId;
        }

        @Override
        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getDockerHost() {
            return dockerHost;
        }

        public void setDockerHost(String dockerHost) {
            this.dockerHost = dockerHost;
        }

        public boolean getAutoremoveContainer() {
            return autoremoveContainer;
        }

        public void setAutoremoveContainer(boolean autoremoveContainer) {
            this.autoremoveContainer = autoremoveContainer;
        }
    }
}
