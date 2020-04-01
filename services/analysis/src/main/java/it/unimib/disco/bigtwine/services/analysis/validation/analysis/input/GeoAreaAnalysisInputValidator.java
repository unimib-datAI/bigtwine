package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.BoundingBox;
import it.unimib.disco.bigtwine.services.analysis.domain.GeoAreaAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;

import java.util.List;

public class GeoAreaAnalysisInputValidator implements AnalysisInputValidator {
    @Override
    public void validate(AnalysisInput input) throws InvalidAnalysisInputProvidedException {
        if (!(input instanceof GeoAreaAnalysisInput)) {
            throw new InvalidAnalysisInputProvidedException("Geo area input not provided");
        }

        GeoAreaAnalysisInput queryInput = (GeoAreaAnalysisInput)input;
        List<BoundingBox> bboxes = queryInput.getBoundingBoxes();

        if (bboxes == null || bboxes.isEmpty()) {
            throw new InvalidAnalysisInputProvidedException("Invalid geo area input, no bounding boxes provided");
        }
    }
}
