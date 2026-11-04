package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class SampleCountIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> source;
    private final Random random;

    private BigInteger elementsLeft;
    private BigInteger samplesLeft;
    private T nextValue;
    private boolean ready;

    public SampleCountIterator(Iterator<? extends T> source, BigInteger sourceSize, BigInteger sampleSize) {
        if (sourceSize.signum() < 0 || sampleSize.signum() < 0) {
            throw new IllegalArgumentException("Sizes must be nonnegative");
        }
        this.source = source;
        this.random = new Random();
        this.elementsLeft = sourceSize;
        this.samplesLeft = sourceSize.min(sampleSize);
    }

    @Override
    public boolean hasNext() {
        if (ready) {
            return true;
        }

        while (samplesLeft.signum() > 0 && elementsLeft.signum() > 0) {
            if (!source.hasNext()) {
                throw new IllegalStateException("Source is shorter than the declared size");
            }

            T candidate = source.next();
            boolean selected = randomBelow(elementsLeft, random).compareTo(samplesLeft) < 0;

            elementsLeft = elementsLeft.subtract(BigInteger.ONE);

            if (selected) {
                nextValue = candidate;
                ready = true;
                return true;
            }
        }

        return false;
    }

    private BigInteger randomBelow(BigInteger bound, Random random) {
        if (bound.signum() <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        BigInteger value;
        do {
            value = new BigInteger(bound.bitLength(), random);
        } while (value.compareTo(bound) >= 0);

        return value;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        T result = nextValue;
        nextValue = null;
        ready = false;
        samplesLeft = samplesLeft.subtract(BigInteger.ONE);
        return result;
    }
}
