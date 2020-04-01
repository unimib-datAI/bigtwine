package it.unimib.disco.bigtwine.commons.processors.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileProcessor<I> {
    File getOutputDirectory();
    void setOutputDirectory(File outputDirectory);
    File getInputDirectory();
    void setInputDirectory(File inputDirectory);
    File getWorkingDirectory();
    void setWorkingDirectory(File workingDirectory);
    default boolean setupWorkingDirectory() {
        boolean res = true;
        for (File dir : new File[] {this.getInputDirectory(), this.getOutputDirectory()}) {
            if (dir == null) continue;

            if (!dir.exists()) {
                try {
                    res &= dir.mkdirs();
                }catch (SecurityException e) {
                    return false;
                }
            }
        }

        return res;
    }
    default File makeInputFile(String tag) {
        Path inputFilePath = Paths.get(this.getInputDirectory().toString(), tag);
        return inputFilePath.toFile();
    }
    default File makeOutputFile(String tag) {
        Path outputFilePath = Paths.get(this.getOutputDirectory().toString(), tag);
        return outputFilePath.toFile();
    }

    boolean generateInputFile(File file, I[] items);
    void processOutputFile(File file);
}
