package it.unimib.disco.bigtwine.services.nel.parsers;

import it.unimib.disco.bigtwine.commons.csv.CSVFactory;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

public class Mind2016OutputParserTest {

    @Test
    public void testParseSingle() {
        String input = "100259039354687488\t0\t7\thttp://dbpedia.org/resource/Channel_4\t0.61\tThing";
        StringReader reader = new StringReader(input);

        OutputParser parser = new Mind2016OutputParser(CSVFactory.getFactory());
        parser.setReader(reader);

        LinkedText[] tweets = parser.items();

        assertEquals(1, tweets.length);
        assertEquals(1, tweets[0].getEntities().length);
        assertEquals("100259039354687488", tweets[0].getTag());
        assertEquals(0, tweets[0].getEntity(0).getPosition().getStart());
        assertEquals(7, tweets[0].getEntity(0).getPosition().getEnd());
        assertEquals("http://dbpedia.org/resource/Channel_4", tweets[0].getEntity(0).getLink());
        assertFalse(tweets[0].getEntity(0).isNil());
        assertNull(tweets[0].getEntity(0).getNilCluster());
        assertEquals(0.61f, tweets[0].getEntity(0).getConfidence(), 0.01);
        assertEquals("Thing", tweets[0].getEntity(0).getCategory());
    }

    @Test
    public void testParseNil() {
        String input = "94103364815699969\t0\t3\tNIL1\t1\tThing";
        StringReader reader = new StringReader(input);

        OutputParser parser = new Mind2016OutputParser(CSVFactory.getFactory());
        parser.setReader(reader);

        LinkedText[] tweets = parser.items();

        assertEquals(1, tweets.length);
        assertEquals(1, tweets[0].getEntities().length);
        assertEquals("94103364815699969", tweets[0].getTag());
        assertEquals(0, tweets[0].getEntity(0).getPosition().getStart());
        assertEquals(3, tweets[0].getEntity(0).getPosition().getEnd());
        assertTrue(tweets[0].getEntity(0).isNil());
        assertEquals("NIL1", tweets[0].getEntity(0).getNilCluster());
        assertNull(tweets[0].getEntity(0).getLink());
        assertEquals(1, tweets[0].getEntity(0).getConfidence(), 0.01);
        assertEquals("Thing", tweets[0].getEntity(0).getCategory());
    }

    @Test
    public void testParseMultiple() {
        String input = "93656691425554432\t0\t7\thttp://dbpedia.org/resource/David_Cameron\t0.64\tPerson\n" +
            "93656691425554432\t101\t108\thttp://dbpedia.org/resource/Tory\t0.66\tThing\n" +
            "94103364815699969\t0\t3\tNIL1\t1\tThing\n" +
            "101428194460180480\t8\t37\thttp://dbpedia.org/resource/Cathay_Pacific\t0.58\tOrganization\n" +
            "96997663056207872\t0\t39\thttp://dbpedia.org/resource/Sony\t0.51\tOrganization\n" +
            "96997663056207872\t44\t67\thttp://dbpedia.org/resource/Sony\t0.54\tOrganization\n";
        StringReader reader = new StringReader(input);

        OutputParser parser = new Mind2016OutputParser(CSVFactory.getFactory());
        parser.setReader(reader);

        LinkedText[] tweets = parser.items();

        assertEquals(4, tweets.length);
        assertEquals(2, tweets[0].getEntities().length);
        assertEquals(1, tweets[1].getEntities().length);
        assertEquals(1, tweets[2].getEntities().length);
        assertEquals(2, tweets[3].getEntities().length);

        assertEquals("93656691425554432", tweets[0].getTag());
        assertEquals("94103364815699969", tweets[1].getTag());
        assertEquals("101428194460180480", tweets[2].getTag());
        assertEquals("96997663056207872", tweets[3].getTag());

        assertTrue(tweets[1].getEntity(0).isNil());
    }
}
