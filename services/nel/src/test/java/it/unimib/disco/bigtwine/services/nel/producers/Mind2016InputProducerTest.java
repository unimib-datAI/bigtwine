package it.unimib.disco.bigtwine.services.nel.producers;

import it.unimib.disco.bigtwine.services.nel.domain.NamedEntity;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.services.nel.Linker;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class Mind2016InputProducerTest {

    @Test
    public void testMind2016ProducerSingle() throws IOException {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setLinker(Linker.mind2016)
            .setWriter(writer)
            .build();

        NamedEntity entity1 = new NamedEntity();
        entity1.setValue("Lamar Odom");
        entity1.setLabel("person");
        entity1.setProbability(5.0f);

        RecognizedText tweet1 = new RecognizedText();
        tweet1.setTag("93314579924393984");
        tweet1.setText("Lamar Odom's Car Accident Claims Life Of A 15 Year Old Boy http://on.vh1.com/pJObAI");
        tweet1.setEntities(new NamedEntity[] {
            entity1
        });

        producer.append(tweet1);
        producer.close();

        String expected = "[#ID#]\t93314579924393984\n" +
            "[#ETS#]\tLamar Odom\tperson\t5.0\n" +
            "[#TWEET#]\tLamar Odom's Car Accident Claims Life Of A 15 Year Old Boy http://on.vh1.com/pJObAI\n" +
            "\n";
        assertEquals(expected, writer.toString());
    }

    @Test
    public void testMind2016ProducerMultipleEntities() throws IOException {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setLinker(Linker.mind2016)
            .setWriter(writer)
            .build();

        NamedEntity entity1 = new NamedEntity();
        entity1.setValue("RB Willis McGahee");
        entity1.setLabel("other");
        entity1.setProbability(5.0f);

        NamedEntity entity2 = new NamedEntity();
        entity2.setValue("Denver");
        entity2.setLabel("geo-loc");
        entity2.setProbability(6.0f);

        RecognizedText tweet1 = new RecognizedText();
        tweet1.setTag("96976835820269568");
        tweet1.setText("RB Willis McGahee reaches agreement with Denver on a 3-yr deal for $7,500,000, including $3 million guaranteed.");
        tweet1.setEntities(new NamedEntity[] {
            entity1, entity2
        });

        producer.append(tweet1);
        producer.close();

        String expected = "[#ID#]\t96976835820269568\n" +
            "[#ETS#]\tRB Willis McGahee\tother\t5.0\n" +
            "[#ETS#]\tDenver\tgeo-loc\t6.0\n" +
            "[#TWEET#]\tRB Willis McGahee reaches agreement with Denver on a 3-yr deal for $7,500,000, including $3 million guaranteed.\n" +
            "\n";
        assertEquals(expected, writer.toString());
    }

    @Test
    public void testMind2016ProducerMultipleTweets() throws IOException {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setLinker(Linker.mind2016)
            .setWriter(writer)
            .build();

        NamedEntity entity1 = new NamedEntity();
        entity1.setValue("RB Willis McGahee");
        entity1.setLabel("other");
        entity1.setProbability(5.0f);

        NamedEntity entity2 = new NamedEntity();
        entity2.setValue("Denver");
        entity2.setLabel("geo-loc");
        entity2.setProbability(6.0f);

        NamedEntity entity3 = new NamedEntity();
        entity3.setValue("Lamar Odom");
        entity3.setLabel("person");
        entity3.setProbability(5.0f);

        RecognizedText tweet1 = new RecognizedText();
        tweet1.setTag("96976835820269568");
        tweet1.setText("RB Willis McGahee reaches agreement with Denver on a 3-yr deal for $7,500,000, including $3 million guaranteed.");
        tweet1.setEntities(new NamedEntity[] {
            entity1, entity2
        });

        RecognizedText tweet2 = new RecognizedText();
        tweet2.setTag("93314579924393984");
        tweet2.setText("Lamar Odom's Car Accident Claims Life Of A 15 Year Old Boy http://on.vh1.com/pJObAI");
        tweet2.setEntities(new NamedEntity[] {
            entity3
        });

        producer.append(new RecognizedText[]{
            tweet1, tweet2
        });
        producer.close();

        String expected = "[#ID#]\t96976835820269568\n" +
            "[#ETS#]\tRB Willis McGahee\tother\t5.0\n" +
            "[#ETS#]\tDenver\tgeo-loc\t6.0\n" +
            "[#TWEET#]\tRB Willis McGahee reaches agreement with Denver on a 3-yr deal for $7,500,000, including $3 million guaranteed.\n" +
            "\n" +
            "[#ID#]\t93314579924393984\n" +
            "[#ETS#]\tLamar Odom\tperson\t5.0\n" +
            "[#TWEET#]\tLamar Odom's Car Accident Claims Life Of A 15 Year Old Boy http://on.vh1.com/pJObAI\n" +
            "\n";
        assertEquals(expected, writer.toString());
    }
}
