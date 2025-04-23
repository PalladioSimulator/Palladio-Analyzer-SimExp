package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.HashMap;
import java.util.Map;

public class Counter<T> {
    private final Map<T, Integer> countMap = new HashMap<>();

    public void add(T item) {
        countMap.put(item, countMap.getOrDefault(item, 0) + 1);
    }

    public int size() {
        return countMap.size();
    }

    public int get(T item) {
        return countMap.getOrDefault(item, 0);
    }

    @Override
    public String toString() {
        return countMap.toString();
    }
}