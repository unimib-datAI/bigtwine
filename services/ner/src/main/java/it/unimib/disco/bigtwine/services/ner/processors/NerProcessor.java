package it.unimib.disco.bigtwine.services.ner.processors;

import it.unimib.disco.bigtwine.services.ner.domain.PlainText;
import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.services.ner.Recognizer;

public interface NerProcessor extends GenericProcessor<PlainText, RecognizedText> {

    Recognizer getRecognizer();

}
