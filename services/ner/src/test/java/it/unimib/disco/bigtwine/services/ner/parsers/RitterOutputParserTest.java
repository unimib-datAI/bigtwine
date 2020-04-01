package it.unimib.disco.bigtwine.services.ner.parsers;

import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class RitterOutputParserTest {
    @Test
    public void testParseSingle() {
        String input = String.join("\n",
            "[#ID#]\t536886411509899000",
            "[#ETS#]\tIbiza \tperson \t0.666666666667",
            "[#ETS#]\tIbiza \tgeo-loc \t1.33333333333",
            "[#ETS#]\tIbiza \ttvshow \t0.333333333333",
            "[#TWEET#]\t£100 return flights to Ibiza In the middle of July???? Sureeeeeeely\uD83D\uDC83",
            ""
        );

        RitterOutputParser parser = new RitterOutputParser(input);
        RecognizedText[] tweets = parser.tweets();

        assertEquals(1, tweets.length);

        RecognizedText tweet = tweets[0];

        assertEquals("536886411509899000", tweet.getTag());
        assertEquals("£100 return flights to Ibiza In the middle of July???? Sureeeeeeely\uD83D\uDC83", tweet.getText());
        assertEquals(3, tweet.getEntities().length);
        assertEquals("Ibiza", tweet.getEntity(1).getValue());
        assertEquals("geo-loc", tweet.getEntity(1).getLabel());
        assertEquals(133, (int)(tweet.getEntity(1).getProbability() * 100));
    }

    @Test
    public void testParseMultiple() throws IOException {
        String input = String.join("\n",
            "[#ID#]\t536886411509899000",
            "[#ETS#]\tIbiza \tperson \t0.666666666667",
            "[#ETS#]\tIbiza \tgeo-loc \t1.33333333333",
            "[#ETS#]\tIbiza \ttvshow \t0.333333333333",
            "[#TWEET#]\t£100 return flights to Ibiza In the middle of July???? Sureeeeeeely\uD83D\uDC83",
            "",
            "[#ID#]\t378061589586640000",
            "[#ETS#]\t#Syria \tband \t0.5",
            "[#ETS#]\t#Syria \tgeo-loc \t3.0",
            "[#ETS#]\t#Lebanon \tband \t0.5",
            "[#ETS#]\t#Lebanon \tgeo-loc \t3.0",
            "[#TWEET#]\tRT @Refugees: Touching video of #Syria refugees leaving #Lebanon yesterday for protection &amp; medical aid in #Germany http://t.co/sdHpXkqyn0",
            "",
            "[#ID#]\t539008076347899000",
            "[#TWEET#]\t@thearsenalhorse but not surprising considering you spent your whole evening raging because a couple of fans showed a banner.",
            ""
        );

        RitterOutputParser parser = new RitterOutputParser(input);
        RecognizedText[] tweets = parser.tweets();

        assertEquals(3, tweets.length);
        assertEquals(3, tweets[0].getEntities().length);
        assertEquals(4, tweets[1].getEntities().length);
        assertEquals(0, tweets[2].getEntities().length);
    }

    @Test
    public void testParseFile() throws IOException {
        File file = new File("src/test/resources/samples/ner-test-input.txt");

        assertTrue(file.exists());

        RitterOutputParser parser = new RitterOutputParser(file);
        RecognizedText[] tweets = parser.tweets();

        assertEquals(6025, tweets.length);
    }
}
