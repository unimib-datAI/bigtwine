package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TwitterNeelUtil {

    private static final Logger log = LoggerFactory.getLogger(TwitterNeelUtil.class);

    public static String flattifyAnalysisInput(AnalysisInfo analysis) {
        try {
            if (analysis.isQueryInputType()) {
                String joinOperator = ((String) analysis.getInput()
                    .get(AnalysisInfo.InputKeys.JOIN_OPERATOR))
                    .toLowerCase();
                @SuppressWarnings("unchecked")
                List<String> tokens = (List<String>) analysis.getInput().get(AnalysisInfo.InputKeys.TOKENS);

                if (joinOperator.equals("any")) {
                    return String.join(",", tokens);
                } else if (joinOperator.equals("all")) {
                    return String.join(" ", tokens);
                } else {
                    log.debug("Cannot flattify query input, Invalid join operator: {}", joinOperator);
                    return null;
                }
            } else if (analysis.isGeoAreaInputType()) {
                List<Double> bboxCoords = new ArrayList<>();

                try {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> bboxes = (List<Map<String, Object>>) analysis
                        .getInput()
                        .get(AnalysisInfo.InputKeys.BOUNDING_BOXES);

                    for (Map<String, Object> bbox: bboxes) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> swCoords = (Map<String, Object>)bbox.get(AnalysisInfo.InputKeys.SOUTHWEST_CORDS);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> neCoords = (Map<String, Object>)bbox.get(AnalysisInfo.InputKeys.NORTHEAST_CORDS);

                        bboxCoords.addAll(
                            Arrays.asList(
                                (Double)swCoords.get(AnalysisInfo.InputKeys.LONGITUDE),
                                (Double)swCoords.get(AnalysisInfo.InputKeys.LATITUDE),
                                (Double)neCoords.get(AnalysisInfo.InputKeys.LONGITUDE),
                                (Double)neCoords.get(AnalysisInfo.InputKeys.LATITUDE)
                            ));
                    }
                } catch (Exception e) {
                    log.error("Input flattify error", e);
                    return null;
                }

                return StringUtils.join(bboxCoords, ",");
            } else if (analysis.isDatasetInputType()) {
                return (String) analysis.getInput().get(AnalysisInfo.InputKeys.DOCUMENT_ID);
            } else {
                log.debug("Invalid input type: {}", analysis.getInput().get(AnalysisInfo.InputKeys.TYPE));
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
