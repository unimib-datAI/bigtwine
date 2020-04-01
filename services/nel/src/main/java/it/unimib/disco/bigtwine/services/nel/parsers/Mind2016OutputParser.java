package it.unimib.disco.bigtwine.services.nel.parsers;

import it.unimib.disco.bigtwine.commons.csv.CSVFactory;
import it.unimib.disco.bigtwine.commons.csv.CSVReader;
import it.unimib.disco.bigtwine.commons.csv.CSVRecord;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedEntity;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import it.unimib.disco.bigtwine.services.nel.domain.TextRange;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class Mind2016OutputParser implements OutputParser {

    private Reader reader;
    private CSVFactory csvFactory;
    private CSVReader csvReader;
    private static final char delimiter = '\t';
    private CSVRecord nextRecord;
    private LinkedText nextTweet;

    public Mind2016OutputParser(CSVFactory csvFactory) {
        this.csvFactory = csvFactory;
    }


    @Override
    public Reader getReader() {
        return reader;
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public CSVReader getCsvReader() throws IOException {
        if (this.csvReader == null) {
            this.csvReader = this.csvFactory.getReader(reader, delimiter);
        }

        return this.csvReader;
    }

    private boolean isValidTweet(LinkedText tweet) {
        return true;
    }

    private LinkedEntity parseEntity(CSVRecord record) {
        if (record.size() == 0) return null;

        try {
            int posStart = Integer.parseInt(record.get(1));
            int posEnd = Integer.parseInt(record.get(2));
            String linkOrNilCluster = record.get(3).trim();
            float confidence = Float.parseFloat(record.get(4));
            String category = record.get(5).trim();
            boolean isNil = linkOrNilCluster.toUpperCase().startsWith("NIL");

            return new LinkedEntity(
                new TextRange(posStart, posEnd),
                linkOrNilCluster,
                confidence,
                category,
                isNil);

        }catch(IllegalArgumentException e) {
            return null;
        }
    }

    private LinkedText parse() throws IOException {
        Iterator<CSVRecord> csv = this.getCsvReader().iterator();

        if (this.nextTweet == null) {
            this.nextTweet = new LinkedText();
        }

        CSVRecord next;
        if (this.nextRecord != null) {
            next = this.nextRecord;
            this.nextRecord = null;
        }else if (csv.hasNext()) {
            next = csv.next();
        }else {
            return null;
        }

        List<LinkedEntity> entities = new ArrayList<>();

        while(next != null) {
            CSVRecord current = next;
            next = null;

            if (current.size() != 6) {
                if (csv.hasNext()) {
                    next = csv.next();
                }
                continue;
            }

            String tweetId = current.get(0).trim();
            if (this.nextTweet.getTag() == null) {
                this.nextTweet.setTag(tweetId);
            }

            if (!tweetId.equals(this.nextTweet.getTag())) {
                this.nextRecord = current;
                break;
            }else if (csv.hasNext()) {
                next = csv.next();
            }

            LinkedEntity entity = this.parseEntity(current);

            if (entity != null) {
                entities.add(entity);
            }
        }

        this.nextTweet.setEntities(entities.toArray(new LinkedEntity[0]));

        if (this.isValidTweet(this.nextTweet)) {
            return this.nextTweet;
        }else {
            return this.parse();
        }
    }

    @Override
    public LinkedText[] items() {
        List<LinkedText> tweets = new ArrayList<>();
        while (this.hasNext()) {
            tweets.add(this.nextTweet);
            this.nextTweet = null;
        }

        return tweets.toArray(new LinkedText[0]);
    }

    @Override
    public boolean hasNext() {
        if (this.nextTweet == null) {
            try {
                this.nextTweet = this.parse();
            } catch (IOException e) {
                this.nextTweet = null;
            }
        }

        return this.nextTweet != null;
    }

    @Override
    public LinkedText next() {
        if (this.hasNext()) {
            LinkedText tweet = this.nextTweet;
            this.nextTweet = null;
            return tweet;
        }

        throw new NoSuchElementException();
    }
}
