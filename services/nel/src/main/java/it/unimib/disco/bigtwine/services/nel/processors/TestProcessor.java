package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedEntity;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.services.nel.domain.TextRange;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.nel.Linker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestProcessor implements Processor {

    public static final Linker linker = Linker.test;

    private ProcessorListener<LinkedText> processorListener;

    private String[] links = new String[] {
        "http://dbpedia.org/resource/Lamar_Odom",
        "http://dbpedia.org/resource/Dow_Jones_&_Company",
        "http://dbpedia.org/resource/Aquarius_(constellation)",
        "http://dbpedia.org/resource/Karma",
        "http://dbpedia.org/resource/Lucas_Glover",
        "http://dbpedia.org/resource/Harry_Potter",
        "http://dbpedia.org/resource/Willis_McGahee",
        "http://dbpedia.org/resource/Denver",
        "http://dbpedia.org/resource/Ladbroke_Grove",
        "http://dbpedia.org/resource/Gruff_Rhys",
        "http://dbpedia.org/resource/Barack_Obama",
        "http://dbpedia.org/resource/Aquarius_(constellation)",
        "http://dbpedia.org/resource/Facebook",
        "http://dbpedia.org/resource/Pee-wee_Herman"
    };

    @Override
    public Linker getLinker() {
        return linker;
    }

    @Override
    public String getProcessorId() {
        return "test-processor";
    }

    @Override
    public void setExecutor(Executor executor) {

    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void setListener(ProcessorListener<LinkedText> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean configureProcessor() {
        return true;
    }

    @Override
    public boolean process(String tag, RecognizedText item) {
        return this.process(tag, new RecognizedText[]{item});
    }

    @Override
    public boolean process(String tag, RecognizedText[] items) {
        List<LinkedText> linkedTexts = new ArrayList<>();
        for (RecognizedText tweet : items) {
            int count = tweet.getEntities().length > 0 ? new Random().nextInt(tweet.getEntities().length) : 0;
            LinkedText lt = new LinkedText(tweet.getTag(), null);
            List<LinkedEntity> entities = new ArrayList<>();
            for (int i = 0; i < count; ++i)  {
                entities.add(new LinkedEntity(
                    new TextRange(0, 1),
                    this.links[new Random().nextInt(this.links.length)],
                    1.0f,
                    "test",
                    false
                ));
            }
            lt.setEntities(entities.toArray(new LinkedEntity[0]));
            linkedTexts.add(lt);
        }
        this.processorListener.onProcessed(this, tag, linkedTexts.toArray(new LinkedText[0]));
        return true;
    }
}
