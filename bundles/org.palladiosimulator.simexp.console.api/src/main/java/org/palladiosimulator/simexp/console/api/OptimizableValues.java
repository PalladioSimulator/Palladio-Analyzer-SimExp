package org.palladiosimulator.simexp.console.api;

import java.util.ArrayList;
import java.util.List;

public class OptimizableValues {
    public abstract static class Entry {
        public String name;
    }

    public static class BoolEntry extends Entry {
        public boolean value;

        public BoolEntry(String name, boolean value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class StringEntry extends Entry {
        public String value;

        public StringEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class IntEntry extends Entry {
        public int value;

        public IntEntry(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class DoubleEntry extends Entry {
        public double value;

        public DoubleEntry(String name, double value) {
            this.name = name;
            this.value = value;
        }
    }

    public List<BoolEntry> boolValues;
    public List<StringEntry> stringValues;
    public List<IntEntry> intValues;
    public List<DoubleEntry> doubleValues;

    public OptimizableValues() {
        this.boolValues = new ArrayList<>();
        this.stringValues = new ArrayList<>();
        this.intValues = new ArrayList<>();
        this.doubleValues = new ArrayList<>();
    }

    public List<Entry> getValues() {
        List<Entry> values = new ArrayList<>();
        values.addAll(boolValues);
        values.addAll(stringValues);
        values.addAll(intValues);
        values.addAll(doubleValues);
        return values;
    }
}
