package org.palladiosimulator.simexp.app.console;

import java.util.ArrayList;
import java.util.List;

public class OptimizableValues {
    public abstract static class Entry {
        String name;
    }

    public static class StringEntry extends Entry {
        String value;
    }

    public static class IntEntry extends Entry {
        int value;
    }

    public List<StringEntry> stringValues;
    public List<IntEntry> intValues;

    public OptimizableValues() {
        this.stringValues = new ArrayList<>();
        this.intValues = new ArrayList<>();
    }

    public List<Entry> getValues() {
        List<Entry> values = new ArrayList<>();
        values.addAll(stringValues);
        values.addAll(intValues);
        return values;
    }
}
