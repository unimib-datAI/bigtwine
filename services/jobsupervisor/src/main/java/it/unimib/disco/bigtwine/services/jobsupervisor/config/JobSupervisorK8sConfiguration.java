package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import io.github.jhipster.config.JHipsterConstants;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.BatchV1Api;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesObjectLoader;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.YamlTemplateKubernetesObjectLoader;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelExportJobExecutableBuilderHelper;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelJobExecutableBuilderHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_K8S)
public class JobSupervisorK8sConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KubernetesJobExecutor.class);

    private ApiClient apiClient;

    private KubernetesObjectLoader createFlinkTwitterNeelKubernetesObjectLoader(ApplicationProperties props) throws IOException, URISyntaxException {
        String templateName = props.getTwitterNeel().getStream().getFlinkJob().getKubernetesTemplate();
        URI templateUri;
        if (Paths.get(templateName).isAbsolute()) {
            templateUri = new URI(templateName);
        } else {
            templateUri = IOUtils
                .resourceToURL(templateName, JobSupervisorK8sConfiguration.class.getClassLoader())
                .toURI();
        }

        File template = Paths.get(templateUri).toFile();
        if (!(template.exists() && template.isFile() && template.canRead())) {
            throw new InvalidConfigurationPropertyValueException(
                "application.twitterNeel.stream.flinkJob.kubernetesTemplate",
                templateName,
                "The given template is not accessible or it isn't a file: " + templateUri.toString()
            );
        }

        return new YamlTemplateKubernetesObjectLoader(template);
    }

    @Bean("TWITTER_NEEL")
    public JobExecutableBuilder getFlinkTwitterNeelKubernetesJobExecutableBuilder(
        FlinkTwitterNeelJobExecutableBuilderHelper helper,
        ApplicationProperties props) throws IOException, URISyntaxException {
        KubernetesObjectLoader kubernetesObjectLoader = createFlinkTwitterNeelKubernetesObjectLoader(props);

        return new KubernetesJobExecutableBuilder(kubernetesObjectLoader, helper);
    }

    @Bean("TWITTER_NEEL__EXPORT")
    public JobExecutableBuilder getFlinkTwitterNeelExportKubernetesJobExecutableBuilder(
        FlinkTwitterNeelExportJobExecutableBuilderHelper helper,
        ApplicationProperties props) throws IOException, URISyntaxException {
        KubernetesObjectLoader kubernetesObjectLoader = createFlinkTwitterNeelKubernetesObjectLoader(props);

        return new KubernetesJobExecutableBuilder(kubernetesObjectLoader, helper);
    }

    @Bean
    public ApiClient getKubernetesApiClient() throws IOException {
        ApiClient client = io.kubernetes.client.util.Config.fromCluster();
        io.kubernetes.client.Configuration.setDefaultApiClient(client);

        return client;
    }

    @Bean
    public JobExecutor getKubernetesJobExecutor(ApplicationProperties props) throws IOException {
        String namespace = props.getKubernetes().getNamespace();
        if (this.apiClient == null) {
            ApiClient client = this.getKubernetesApiClient();
            checkKubernetesAuthorization(client, namespace);
            this.apiClient = client;
        }

        return new KubernetesJobExecutor(this.apiClient, namespace);
    }

    private void checkKubernetesAuthorization(ApiClient apiClient, String namespace) {
        try {
            BatchV1Api api = new BatchV1Api(apiClient);
            api.listNamespacedJob(namespace, null, null, null, null, null, null, null, null);
        } catch (Exception e) {
            log.error("Current service account cannot manage Kubernetes jobs in namespace '{}'", namespace, e);
            throw new RuntimeException("Current service account cannot manage Kubernetes jobs in namespace: '" +
                namespace + "', nested exception " +
                e.getLocalizedMessage());
        }
    }
}
