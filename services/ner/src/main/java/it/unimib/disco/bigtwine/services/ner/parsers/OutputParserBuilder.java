package it.unimib.disco.bigtwine.services.ner.parsers;

import it.unimib.disco.bigtwine.services.ner.Recognizer;

import java.io.*;

public class OutputParserBuilder {

    private Recognizer recognizer;

    private Reader reader;

    public static OutputParserBuilder getDefaultBuilder() {
        return new OutputParserBuilder();
    }

    public OutputParserBuilder setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
        return this;
    }

    public Recognizer getRecognizer() {
        return recognizer;
    }

    public Reader getReader() {
        return reader;
    }

    public OutputParserBuilder setReader(Reader reader) {
        this.reader = reader;
        return this;
    }

    public OutputParserBuilder setInput(String string) {
        this.reader = new StringReader(string);
        return this;
    }

    public OutputParserBuilder setInput(File file) {
        try {
            this.reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            this.reader = null;
        }
        return this;
    }

    public OutputParser build() {
        if (this.recognizer == null) {
            return null;
        }

        if (this.reader == null) {
            return null;
        }

        OutputParser outputParser;
        switch (this.recognizer) {
            case ritter:
                outputParser = new RitterOutputParser();
                break;
            default:
                return null;
        }

        outputParser.setReader(this.reader);

        return outputParser;
    }
}
