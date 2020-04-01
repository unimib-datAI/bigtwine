package it.unimib.disco.bigtwine.services.nel;

import javax.validation.constraints.NotNull;

public enum Linker {
    mind2016, test;

    private static Linker defaultLinker= mind2016;

    public static Linker getDefault() {
        return defaultLinker;
    }
    public static void setDefault(@NotNull Linker linker) {
        defaultLinker = linker;
    }
}
