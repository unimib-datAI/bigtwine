package it.unimib.disco.bigtwine.services.ner.producers;

import it.unimib.disco.bigtwine.services.ner.Recognizer;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class InputProducerBuilderTest {

    @Test
    public void testBuildRitter() throws Exception {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setRecognizer(Recognizer.ritter)
            .setWriter(writer)
            .build();

        assertEquals(RitterInputProducer.class, producer.getClass());
        assertEquals(writer, producer.getWriter());
    }
}
