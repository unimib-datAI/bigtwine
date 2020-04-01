package it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import it.unimib.disco.bigtwine.services.jobsupervisor.client.SocialsServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.OAuthCredentials;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.UserInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.TwitterNeelUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlinkTwitterNeelJobExecutableBuilderHelper implements JobExecutableBuilder.BuilderHelper {
    private final ApplicationProperties applicationProperties;
    private final SocialsServiceClient socialsServiceClient;

    @SuppressWarnings("UnstableApiUsage")
    private final Escaper SHELL_ESCAPE = Escapers.builder()
        .addEscape('\'', "'\"'\"'")
        .build();

    public FlinkTwitterNeelJobExecutableBuilderHelper(ApplicationProperties applicationProperties, SocialsServiceClient socialsServiceClient) {
        this.applicationProperties = applicationProperties;
        this.socialsServiceClient = socialsServiceClient;
    }

    private OAuthCredentials getTwitterCredentials(Job job) throws JobExecutableBuilder.BuildException {
        if (job.getAnalysis().getOwner() == null) {
            throw new JobExecutableBuilder.BuildException("Job's analysis owner info missing");
        }

        UserInfo analysisOwner = job.getAnalysis().getOwner();
        try {
            return this.socialsServiceClient.findTwitterOAuthCredentials(analysisOwner.getUid());
        }catch (Exception e) {
            throw new JobExecutableBuilder.BuildException("Cannot retrieve twitter credentials for analysis owner: " + analysisOwner.getUid());
        }
    }

    private String flattifyAnalysisInput(AnalysisInfo analysis) throws JobExecutableBuilder.BuildException {
        String flatInput = TwitterNeelUtil.flattifyAnalysisInput(analysis);

        if (flatInput == null) {
            throw new JobExecutableBuilder
                .BuildException(String.format("Cannot flattify analysis input: %s}", analysis.getInput()));
        }

        return flatInput;
    }

    @Override
    public List<String> buildExecutableCommand(Job job) throws JobExecutableBuilder.BuildException {
        AnalysisInfo analysis = job.getAnalysis();
        String javaBin, jarName;

        if (analysis.isStreamAnalysis()) {
            javaBin = this.applicationProperties
                .getTwitterNeel()
                .getStream()
                .getFlinkJob()
                .getJavaBin();
            jarName = this.applicationProperties
                .getTwitterNeel()
                .getStream()
                .getFlinkJob()
                .getJarName();
        } else if (analysis.isDatasetInputType()) {
            javaBin = this.applicationProperties
                .getTwitterNeel()
                .getDataset()
                .getFlinkJob()
                .getJavaBin();
            jarName = this.applicationProperties
                .getTwitterNeel()
                .getDataset()
                .getFlinkJob()
                .getJarName();
        } else {
            throw new UnsupportedOperationException();
        }

        return Arrays.asList(javaBin, "-cp", jarName);
    }

    @Override
    public List<String> buildExecutableArgs(Job job) throws JobExecutableBuilder.BuildException {
        AnalysisInfo analysis = job.getAnalysis();

        String className;

        if (analysis.isStreamAnalysis()) {
            className = this.applicationProperties
                .getTwitterNeel()
                .getStream()
                .getFlinkJob()
                .getJarClass();
        } else if (analysis.isDatasetInputType()) {
            className = this.applicationProperties
                .getTwitterNeel()
                .getDataset()
                .getFlinkJob()
                .getJarClass();
        } else {
            throw new UnsupportedOperationException("Unsupported analysis input type: " + analysis.getInputType());
        }

        List<String> args = new ArrayList<>(Arrays.asList(
            className,
            "--job-id", job.getId(),
            "--analysis-id", analysis.getId()
        ));

        if (analysis.getSettings() != null) {
            Object nerRecognizer = analysis.getSettings().get("ner-recognizer");
            if (nerRecognizer instanceof String && StringUtils.isNotBlank((String)nerRecognizer)) {
                Collections.addAll(args, "--ner-recognizer", nerRecognizer.toString());
            }

            Object nelLinker = analysis.getSettings().get("nel-linker");
            if (nelLinker instanceof String && StringUtils.isNotBlank((String)nelLinker)) {
                Collections.addAll(args, "--nel-linker", nelLinker.toString());
            }

            Object geoDecoder = analysis.getSettings().get("geo-decoder");
            if (geoDecoder instanceof String && StringUtils.isNotBlank((String)geoDecoder)) {
                Collections.addAll(args, "--geo-decoder", geoDecoder.toString());
            }
        }

        if (analysis.isStreamAnalysis()) {
            OAuthCredentials credentials = getTwitterCredentials(job);
            String streamLang = this.applicationProperties.getTwitterNeel().getStream().getDefaultLang();
            String streamSkipRetweets = this.applicationProperties.getTwitterNeel().getStream().isSkipRetweets() ? "true" : "false";
            String streamSampling = String.valueOf(this.applicationProperties.getTwitterNeel().getStream().getSampling());
            String streamHeartbeat = String.valueOf(this.applicationProperties.getTwitterNeel().getStream().getHeartbeat());

            Collections.addAll(args,
                "--twitter-token", credentials.getAccessToken(),
                "--twitter-token-secret", credentials.getAccessTokenSecret(),
                "--twitter-consumer-key", credentials.getConsumerKey(),
                "--twitter-consumer-secret", credentials.getConsumerSecret(),
                "--heartbeat-interval", streamHeartbeat);

            if (analysis.getSettings() != null) {
                Object streamLangSet = analysis.getSettings().get("twitter-stream-lang");
                if (streamLangSet instanceof String && StringUtils.isNotBlank((String)streamLangSet)) {
                    streamLang = (String)streamLangSet;
                }

                Object streamSamplingSet = analysis.getSettings().get("twitter-stream-sampling");
                if (streamSamplingSet instanceof Integer) {
                    streamSampling = String.valueOf(streamSamplingSet);
                }

                Object streamSkipRetweetsSet = analysis.getSettings().get("twitter-stream-skip-retweets");
                if (streamSkipRetweetsSet instanceof Boolean) {
                    streamSkipRetweets = String.valueOf(streamSkipRetweetsSet);
                }
            }

            Collections.addAll(args,
                "--twitter-stream-lang", streamLang,
                "--twitter-stream-sampling", streamSampling,
                "--twitter-skip-retweets", streamSkipRetweets);
        }

        if (analysis.isQueryInputType()) {
            String query = this.flattifyAnalysisInput(analysis);

            Collections.addAll(args,
                "--twitter-stream-query", SHELL_ESCAPE.escape(query)
            );
        } else if (analysis.isGeoAreaInputType()) {
            String boundingBoxes = this.flattifyAnalysisInput(analysis);

            Collections.addAll(args,
                "--twitter-stream-locations", SHELL_ESCAPE.escape(boundingBoxes)
            );
        } else if (analysis.isDatasetInputType()) {
            String documentId = this.flattifyAnalysisInput(analysis);
            String heartbeatInterval = String.valueOf(this.applicationProperties.getTwitterNeel().getDataset().getHeartbeat());

            Collections.addAll(args,
                "--heartbeat-interval", heartbeatInterval,
                "--dataset-document-id", documentId
            );
        } else {
            throw new UnsupportedOperationException("Unsupported analysis input type: " + analysis.getInputType());
        }

        return args;
    }
}
