package it.unimib.disco.bigtwine.services.ner.executors;

import org.junit.Test;

import java.io.File;

public class RitterDockerExecutorTest {

    @Test
    public void testContainerRun() {
        RitterDockerExecutor executor = new RitterDockerExecutor();
        executor.setInputWorkingDirectory(new File("/Users/Fausto/Desktop/ner/input"));
        executor.setOutputWorkingDirectory(new File("/Users/Fausto/Desktop/ner/output"));
        executor.run();
    }
}
