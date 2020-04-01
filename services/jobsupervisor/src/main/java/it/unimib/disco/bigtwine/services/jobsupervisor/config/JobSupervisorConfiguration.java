package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import it.unimib.disco.bigtwine.services.jobsupervisor.client.SocialsServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.context.ContextProvider;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilderBeanLocator;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilderLocator;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelExportJobExecutableBuilderHelper;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelJobExecutableBuilderHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobSupervisorConfiguration {
    @Bean
    public JobExecutableBuilderLocator getJobExecutableBuilderLocator(ContextProvider contextProvider) {
        return new JobExecutableBuilderBeanLocator(contextProvider);
    }

    @Bean
    public FlinkTwitterNeelJobExecutableBuilderHelper getFlinkTwitterNeelHelper(ApplicationProperties applicationProperties,
                                                                                SocialsServiceClient socialsServiceClient) {
        return new FlinkTwitterNeelJobExecutableBuilderHelper(applicationProperties, socialsServiceClient);
    }

    @Bean
    public FlinkTwitterNeelExportJobExecutableBuilderHelper getFlinkTwitterNeelExportHelper(ApplicationProperties applicationProperties) {
        return new FlinkTwitterNeelExportJobExecutableBuilderHelper(applicationProperties);
    }
}
