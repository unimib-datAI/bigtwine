package it.unimib.disco.bigtwine.services.ner.producers;

import it.unimib.disco.bigtwine.services.ner.domain.PlainText;
import it.unimib.disco.bigtwine.services.ner.Recognizer;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringWriter;

public class RitterInputProducerTest {

    @Test
    public void testProduceSingle() throws Exception {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setRecognizer(Recognizer.ritter)
            .setWriter(writer)
            .build();

        PlainText tweet = new PlainText();
        tweet.setTag("1");
        tweet.setText("prova");
        producer.append(tweet);

        producer.close();

        String output = writer.toString();

        assertEquals("1\tprova\r\n", output);
    }

    @Test
    public void testProduceMultiple() throws Exception {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setRecognizer(Recognizer.ritter)
            .setWriter(writer)
            .build();

        PlainText tweet1 = new PlainText();
        tweet1.setTag("1");
        tweet1.setText("prova1");
        PlainText tweet2 = new PlainText();
        tweet2.setTag("2");
        tweet2.setText("prova2");
        producer.append(new PlainText[]{tweet1, tweet2});

        producer.close();

        String output = writer.toString();

        assertEquals("1\tprova1\r\n2\tprova2\r\n", output);
    }
}
