package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class SampleCountIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> source;
    private final Random random;

    private long elementsLeft;
    private int samplesLeft;
    private T nextValue;
    private boolean ready;

    public SampleCountIterator(Iterator<? extends T> source, long sourceSize, int sampleSize) {
        if (sourceSize < 0 || sampleSize < 0) {
            throw new IllegalArgumentException("Sizes must be nonnegative");
        }
        this.source = source;
        this.random = new Random();
        this.elementsLeft = sourceSize;
        this.samplesLeft = (int) Math.min(sourceSize, sampleSize);
    }

    @Override
    public boolean hasNext() {
        if (ready) {
            return true;
        }

        while (samplesLeft > 0 && elementsLeft > 0) {
            if (!source.hasNext()) {
                throw new IllegalStateException("Source is shorter than the declared size");
            }

            T candidate = source.next();
            boolean selected = random.nextLong(elementsLeft) < samplesLeft;

            elementsLeft--;

            if (selected) {
                nextValue = candidate;
                ready = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        T result = nextValue;
        nextValue = null;
        ready = false;
        samplesLeft--;
        return result;
    }
}
