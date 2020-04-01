package it.unimib.disco.bigtwine.services.ner.parsers;

import it.unimib.disco.bigtwine.services.ner.Recognizer;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class OutputParserBuilderTest {
    @Test
    public void testBuildRitter() throws Exception {
        StringReader reader = new StringReader("");
        OutputParser parser = OutputParserBuilder
            .getDefaultBuilder()
            .setRecognizer(Recognizer.ritter)
            .setReader(reader)
            .build();

        assertEquals(RitterOutputParser.class, parser.getClass());
        assertEquals(reader, parser.getReader());
    }
}
