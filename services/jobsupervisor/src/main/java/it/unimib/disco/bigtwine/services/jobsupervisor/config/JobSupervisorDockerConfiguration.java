package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import io.github.jhipster.config.JHipsterConstants;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker.DockerJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.docker.DockerJobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell.ShellJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell.ShellJobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelExportJobExecutableBuilderHelper;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelJobExecutableBuilderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("docker")
public class JobSupervisorDockerConfiguration {

    private ApplicationProperties applicationProperties;

    public JobSupervisorDockerConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public DockerClient getDockerClient() {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();

        String dockerHost = applicationProperties.getDocker().getDockerHost();
        if (dockerHost != null) {
            configBuilder.withDockerHost(applicationProperties.getDocker().getDockerHost());
        }

        return DockerClientBuilder
            .getInstance(configBuilder.build())
            .build();
    }

    @Bean("TWITTER_NEEL")
    public JobExecutableBuilder getFlinkTwitterNeelDockerJobExecutableBuilder(
        FlinkTwitterNeelJobExecutableBuilderHelper helper,
        ApplicationProperties applicationProperties) {
        return new DockerJobExecutableBuilder(helper, applicationProperties.getDocker());
    }

    @Bean("TWITTER_NEEL__EXPORT")
    public JobExecutableBuilder getFlinkTwitterNeelExportDockerJobExecutableBuilder(
        FlinkTwitterNeelExportJobExecutableBuilderHelper helper,
        ApplicationProperties applicationProperties) {
        return new DockerJobExecutableBuilder(helper, applicationProperties.getDocker());
    }

    @Bean
    public JobExecutor getDockerJobExecutor(ApplicationProperties applicationProperties) {
        return new DockerJobExecutor(getDockerClient(), applicationProperties.getDocker());
    }
}
