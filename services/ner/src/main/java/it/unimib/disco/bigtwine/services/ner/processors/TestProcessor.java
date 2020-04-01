package it.unimib.disco.bigtwine.services.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.services.ner.domain.PlainText;
import it.unimib.disco.bigtwine.services.ner.domain.NamedEntity;
import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.ner.Recognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestProcessor implements NerProcessor {

    public static final Recognizer recognizer = Recognizer.test;

    private ProcessorListener<RecognizedText> processorListener;

    @Override
    public Recognizer getRecognizer() {
        return recognizer;
    }

    @Override
    public String getProcessorId() {
        return "test-recognizer";
    }

    @Override
    public void setExecutor(Executor executor) {

    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void setListener(ProcessorListener<RecognizedText> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean configureProcessor() {
        return true;
    }

    @Override
    public boolean process(String tag, PlainText item) {
        return this.process(tag, new PlainText[]{item});
    }

    @Override
    public boolean process(String tag, PlainText[] items) {
        List<RecognizedText> tweets = new ArrayList<>();
        for (PlainText tweet : items) {
            RecognizedText rt = new RecognizedText(tweet.getTag(), tweet.getText());
            List<NamedEntity> entities = new ArrayList<>();
            int count = new Random().nextInt(4);
            for (int i = 0; i < count; ++i)  {
                entities.add(new NamedEntity(
                    "testvalue",
                    "testlabel",
                    1.0f
                ));
            }
            rt.setEntities(entities.toArray(new NamedEntity[0]));
            tweets.add(rt);
        }
        this.processorListener.onProcessed(this, tag, tweets.toArray(new RecognizedText[0]));
        return true;
    }
}
