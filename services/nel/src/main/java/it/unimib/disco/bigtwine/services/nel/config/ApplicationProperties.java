package it.unimib.disco.bigtwine.services.nel.config;

import it.unimib.disco.bigtwine.services.nel.Linker;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Nel.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String defaultLinker = ApplicationDefaults.defaultLinker;
    private final Executors executors = new Executors();
    private final Processors processors = new Processors();

    public static class Processors {
        private final Mind2016 mind2016 = new Mind2016();

        public Mind2016 getMind2016() {
            return mind2016;
        }

        public static class Mind2016 {
            private String workingDirectory = ApplicationDefaults.Processors.Mind2016.workingDirectory;
            private boolean useTmpWorkingDirectory = ApplicationDefaults.Processors.Mind2016.useTmpWorkingDirectory;
            private String fileMonitorSuffixFilter = ApplicationDefaults.Processors.Mind2016.fileMonitorSuffixFilter;
            private String fileMonitorSuffixExclusion = ApplicationDefaults.Processors.Mind2016.fileMonitorSuffixExclusion;

            public String getWorkingDirectory() {
                return workingDirectory;
            }

            public void setWorkingDirectory(String workingDirectory) {
                this.workingDirectory = workingDirectory;
            }

            public boolean isUseTmpWorkingDirectory() {
                return useTmpWorkingDirectory;
            }

            public void setUseTmpWorkingDirectory(boolean useTmpWorkingDirectory) {
                this.useTmpWorkingDirectory = useTmpWorkingDirectory;
            }

            public String getFileMonitorSuffixFilter() {
                return fileMonitorSuffixFilter;
            }

            public void setFileMonitorSuffixFilter(String fileMonitorSuffixFilter) {
                this.fileMonitorSuffixFilter = fileMonitorSuffixFilter;
            }

            public String getFileMonitorSuffixExclusion() {
                return fileMonitorSuffixExclusion;
            }

            public void setFileMonitorSuffixExclusion(String fileMonitorSuffixExclusion) {
                this.fileMonitorSuffixExclusion = fileMonitorSuffixExclusion;
            }
        }
    }
    public static class Executors {

    }

    public String getDefaultLinker() {
        return defaultLinker;
    }

    public void setDefaultLinker(String defaultLinker) {
        this.defaultLinker = defaultLinker;
        Linker linker = Linker.valueOf(defaultLinker);
        Linker.setDefault(linker);
    }

    public Executors getExecutors() {
        return this.executors;
    }

    public Processors getProcessors() {
        return this.processors;
    }
}
