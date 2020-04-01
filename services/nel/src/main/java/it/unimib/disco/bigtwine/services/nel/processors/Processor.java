package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.services.nel.Linker;

public interface Processor extends GenericProcessor<RecognizedText, LinkedText> {

    Linker getLinker();

}
