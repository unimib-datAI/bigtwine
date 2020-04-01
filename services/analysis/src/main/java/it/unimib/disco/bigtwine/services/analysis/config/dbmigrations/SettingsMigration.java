package it.unimib.disco.bigtwine.services.analysis.config.dbmigrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import it.unimib.disco.bigtwine.services.analysis.config.AnalysisSettingConstants;
import it.unimib.disco.bigtwine.services.analysis.config.Constants;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisDefaultSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingChoice;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingCollection;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingVisibility;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.security.AuthoritiesConstants;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ChangeLog(order = "001")
public class SettingsMigration {
    @ChangeSet(order = "01", id = "addGlobalSettings", author = "fausto")
    public void addGlobalSettings(MongoTemplate mongoTemplate) {
        AnalysisSetting maxConcurrentAnalyses = new AnalysisSetting()
            .name(AnalysisSettingConstants.MAX_CONCURRENT_ANALYSES)
            .label("Max concurrent analyses")
            .description("Max number of concurrent analysis started from an user (-1 to not limit)")
            .type(AnalysisSettingType.NUMBER)
            .visibility(AnalysisSettingVisibility.GLOBAL);
        AnalysisSetting maxExecutionTime = new AnalysisSetting()
            .name(AnalysisSettingConstants.MAX_EXECUTION_TIME)
            .label("Max execution time")
            .description("Max number of seconds until automatically stop an analyses (-1 to not limit)")
            .type(AnalysisSettingType.NUMBER)
            .visibility(AnalysisSettingVisibility.GLOBAL);

        mongoTemplate.save(maxConcurrentAnalyses);
        mongoTemplate.save(maxExecutionTime);

        AnalysisDefaultSetting maxExecutionTimeDefault = new AnalysisDefaultSetting()
            .setting(maxExecutionTime)
            .defaultValue(AnalysisSettingConstants.DEFAULT_MAX_EXECUTION_TIME)
            .userCanOverride(false)
            .priority(100);

        AnalysisDefaultSetting maxExecutionTimeAdmin = new AnalysisDefaultSetting()
            .setting(maxConcurrentAnalyses)
            .defaultValue(AnalysisSettingConstants.DEFAULT_MAX_EXECUTION_TIME_ADMIN)
            .userRoles(new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN)))
            .userCanOverride(false)
            .priority(200);

        AnalysisDefaultSetting maxConcurrentAnalysesDefault = new AnalysisDefaultSetting()
            .setting(maxConcurrentAnalyses)
            .defaultValue(AnalysisSettingConstants.DEFAULT_MAX_CONCURRENT_ANALYSES)
            .userCanOverride(false)
            .priority(100);

        AnalysisDefaultSetting maxConcurrentAnalysesAdmin = new AnalysisDefaultSetting()
            .setting(maxConcurrentAnalyses)
            .defaultValue(AnalysisSettingConstants.DEFAULT_MAX_CONCURRENT_ANALYSES_ADMIN)
            .userRoles(new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN)))
            .userCanOverride(false)
            .priority(200);

        mongoTemplate.save(maxExecutionTimeDefault);
        mongoTemplate.save(maxExecutionTimeAdmin);
        mongoTemplate.save(maxConcurrentAnalysesDefault);
        mongoTemplate.save(maxConcurrentAnalysesAdmin);
    }

    @ChangeSet(order = "02", id = "addTwitterNeelSettings", author = "fausto")
    public void addTwitterNeelSettings(MongoTemplate mongoTemplate) {
        // -- SETTINGS
        AnalysisSetting nerRecognizer = new AnalysisSetting()
            .name("ner-recognizer")
            .label("NER Recognizer")
            .visibility(AnalysisSettingVisibility.USER_VISIBLE)
            .type(AnalysisSettingType.SINGLE_CHOICE)
            .addChoice(new AnalysisSettingChoice("Default", "default"));

        AnalysisSetting nelLinker = new AnalysisSetting()
            .name("nel-linker")
            .label("NEL Linker")
            .visibility(AnalysisSettingVisibility.USER_VISIBLE)
            .type(AnalysisSettingType.SINGLE_CHOICE)
            .addChoice(new AnalysisSettingChoice("Default", "default"));

        AnalysisSetting geoDecoder = new AnalysisSetting()
            .name("geo-decoder")
            .label("Geo Decoder")
            .visibility(AnalysisSettingVisibility.USER_VISIBLE)
            .type(AnalysisSettingType.SINGLE_CHOICE)
            .addChoice(new AnalysisSettingChoice("Default", "default"));

        AnalysisSetting twitterStreamLang = new AnalysisSetting()
            .name("twitter-stream-lang")
            .label("Twitter Stream Lang")
            .visibility(AnalysisSettingVisibility.USER_VISIBLE)
            .type(AnalysisSettingType.SINGLE_CHOICE)
            .addChoice(new AnalysisSettingChoice("English", "en"));

        AnalysisSetting twitterStreamSampling = new AnalysisSetting()
            .name("twitter-stream-sampling")
            .label("Twitter Stream Sampling")
            .description("Limit the maximum number of tweets per second")
            .visibility(AnalysisSettingVisibility.USER_VISIBLE)
            .type(AnalysisSettingType.SINGLE_CHOICE)
            .addChoice(new AnalysisSettingChoice("Off", -1))
            .addChoice(new AnalysisSettingChoice("1", 1))
            .addChoice(new AnalysisSettingChoice("3", 3))
            .addChoice(new AnalysisSettingChoice("6", 6));

        AnalysisSetting twitterStreamRetweets = new AnalysisSetting()
            .name("twitter-stream-skip-retweets")
            .label("Twitter Stream Skip Retweets")
            .visibility(AnalysisSettingVisibility.USER_VISIBLE)
            .type(AnalysisSettingType.SINGLE_CHOICE)
            .addChoice(new AnalysisSettingChoice("Yes", true))
            .addChoice(new AnalysisSettingChoice("No", false));

        mongoTemplate.save(nerRecognizer);
        mongoTemplate.save(nelLinker);
        mongoTemplate.save(geoDecoder);
        mongoTemplate.save(twitterStreamLang);
        mongoTemplate.save(twitterStreamSampling);
        mongoTemplate.save(twitterStreamRetweets);

        // -- SETTINGS COLLECTIONS
        Set<AnalysisSetting> tneelDatasetSettingsSet = new HashSet<>(Arrays.asList(
            nerRecognizer,
            nelLinker,
            geoDecoder
        ));

        Set<AnalysisSetting> tneelStreamSettingsSet = new HashSet<>(Arrays.asList(
            nerRecognizer,
            nelLinker,
            geoDecoder,
            twitterStreamLang,
            twitterStreamSampling,
            twitterStreamRetweets
        ));

        AnalysisSettingCollection tneelDatasetSettingCollection = new AnalysisSettingCollection()
            .analysisType(AnalysisType.TWITTER_NEEL)
            .analysisInputType(AnalysisInputType.DATASET)
            .settings(tneelDatasetSettingsSet);

        AnalysisSettingCollection tneelQuerySettingCollection = new AnalysisSettingCollection()
            .analysisType(AnalysisType.TWITTER_NEEL)
            .analysisInputType(AnalysisInputType.QUERY)
            .settings(tneelStreamSettingsSet);

        AnalysisSettingCollection tneelGeoAreaSettingCollection = new AnalysisSettingCollection()
            .analysisType(AnalysisType.TWITTER_NEEL)
            .analysisInputType(AnalysisInputType.GEO_AREA)
            .settings(tneelStreamSettingsSet);

        mongoTemplate.save(tneelDatasetSettingCollection);
        mongoTemplate.save(tneelQuerySettingCollection);
        mongoTemplate.save(tneelGeoAreaSettingCollection);

        // -- SETTING DEFAULTS
        AnalysisDefaultSetting nerRecognizerDefault = new AnalysisDefaultSetting()
            .setting(nerRecognizer)
            .defaultValue("default")
            .userCanOverride(true)
            .priority(100);

        AnalysisDefaultSetting nelLinkerDefault = new AnalysisDefaultSetting()
            .setting(nelLinker)
            .defaultValue("default")
            .userCanOverride(true)
            .priority(100);

        AnalysisDefaultSetting geoDecoderDefault = new AnalysisDefaultSetting()
            .setting(geoDecoder)
            .defaultValue("default")
            .userCanOverride(true)
            .priority(100);

        AnalysisDefaultSetting twitterStreamLangDefault = new AnalysisDefaultSetting()
            .setting(twitterStreamLang)
            .defaultValue("en")
            .userCanOverride(true)
            .priority(100);

        AnalysisDefaultSetting twitterStreamSamplingDefault = new AnalysisDefaultSetting()
            .setting(twitterStreamSampling)
            .defaultValue(-1)
            .userCanOverride(true)
            .priority(100);

        AnalysisDefaultSetting twitterStreamRetweetsDefault = new AnalysisDefaultSetting()
            .setting(twitterStreamRetweets)
            .defaultValue(false)
            .userCanOverride(true)
            .priority(100);

        mongoTemplate.save(nerRecognizerDefault);
        mongoTemplate.save(nelLinkerDefault);
        mongoTemplate.save(geoDecoderDefault);
        mongoTemplate.save(twitterStreamLangDefault);
        mongoTemplate.save(twitterStreamSamplingDefault);
        mongoTemplate.save(twitterStreamRetweetsDefault);
    }
}
