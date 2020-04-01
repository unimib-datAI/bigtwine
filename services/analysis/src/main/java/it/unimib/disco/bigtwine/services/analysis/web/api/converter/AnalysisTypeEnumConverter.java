package it.unimib.disco.bigtwine.services.analysis.web.api.converter;

import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisTypeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AnalysisTypeEnumConverter implements Converter<String, AnalysisTypeEnum> {
    @Override
    public AnalysisTypeEnum convert(String source) {
        return AnalysisTypeEnum.fromValue(source);
    }
}
