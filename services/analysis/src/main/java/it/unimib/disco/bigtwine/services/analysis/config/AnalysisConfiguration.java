package it.unimib.disco.bigtwine.services.analysis.config;

import it.unimib.disco.bigtwine.commons.messaging.dto.NeelProcessedTweetDTO;
import it.unimib.disco.bigtwine.services.analysis.domain.NeelProcessedTweet;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisResultMapperLocator;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisResultPayloadMapperLocator;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.NeelProcessedTweetMapper;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.TwitterNeelAnalysisResultMapper;
import it.unimib.disco.bigtwine.services.analysis.validation.AnalysisExportFormatValidator;
import it.unimib.disco.bigtwine.services.analysis.validation.analysis.input.*;
import it.unimib.disco.bigtwine.services.analysis.validation.AnalysisStatusStaticValidator;
import it.unimib.disco.bigtwine.services.analysis.validation.AnalysisStatusValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class AnalysisConfiguration {

    @Bean
    public AnalysisStatusValidator analysisStatusValidator() {
        return new AnalysisStatusStaticValidator();
    }

    @Bean
    public AnalysisInputValidatorLocator analysisInputValidatorLocator() {
        AnalysisInputValidatorRegistry registry = new AnalysisInputValidatorRegistry();
        registry.registerInputValidator(new QueryAnalysisInputValidator(), AnalysisInputType.QUERY);
        registry.registerInputValidator(new DatasetAnalysisInputValidator(), AnalysisInputType.DATASET);
        registry.registerInputValidator(new GeoAreaAnalysisInputValidator(), AnalysisInputType.GEO_AREA);

        return registry;
    }

    @Bean
    public AnalysisResultPayloadMapperLocator analysisResultPayloadMapperLocator() {
        AnalysisResultPayloadMapperLocator locator = new AnalysisResultPayloadMapperLocator();
        locator.registerMapper(NeelProcessedTweetDTO.class, NeelProcessedTweetMapper.INSTANCE);

        return locator;
    }

    @Bean
    public AnalysisResultMapperLocator analysisResultMapperLocator() {
        AnalysisResultMapperLocator locator = new AnalysisResultMapperLocator();
        locator.registerMapper(NeelProcessedTweet.class, TwitterNeelAnalysisResultMapper.INSTANCE);

        return locator;
    }

    @Bean
    public AnalysisExportFormatValidator analysisExportFormatValidator() {
        AnalysisExportFormatValidator validator = new AnalysisExportFormatValidator();
        validator.registerSupportedFormats(AnalysisType.TWITTER_NEEL, new HashSet<>(
            Arrays.asList("json", "tsv", "twitter-neel-challenge", "twitter-neel-dataset")
        ));

        return validator;
    }

    @Bean
    public AnalysisInputTypeValidator analysisInputTypeValidator() {
        AnalysisInputTypeValidator validator = new AnalysisInputTypeValidator();
        validator.registerSupportedTypes(AnalysisType.TWITTER_NEEL, new HashSet<>(
            Arrays.asList(AnalysisInputType.QUERY, AnalysisInputType.DATASET, AnalysisInputType.GEO_AREA)
        ));

        return validator;
    }
}
