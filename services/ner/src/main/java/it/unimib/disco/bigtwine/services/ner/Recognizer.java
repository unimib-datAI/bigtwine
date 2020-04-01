package it.unimib.disco.bigtwine.services.ner;

import javax.validation.constraints.NotNull;

public enum Recognizer {
    ritter, test;

    private static Recognizer defaultRecognizer = ritter;

    public static Recognizer getDefault() {
        return defaultRecognizer;
    }
    public static void setDefault(@NotNull Recognizer recognizer) {
        defaultRecognizer = recognizer;
    }
}
