package org.palladiosimulator.simexp.app.console.simulation;

import java.util.ArrayList;
import java.util.List;

public class OptimizableValues {
    public abstract static class Entry {
        public String name;
    }

    public static class StringEntry extends Entry {
        public String value;
    }

    public static class IntEntry extends Entry {
        public int value;
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
