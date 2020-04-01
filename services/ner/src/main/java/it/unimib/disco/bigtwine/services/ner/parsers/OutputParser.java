package it.unimib.disco.bigtwine.services.ner.parsers;

import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.parsers.GenericOutputParser;

public interface OutputParser extends GenericOutputParser<RecognizedText> {
    default RecognizedText[] tweets() {
        return this.items();
    }
}
