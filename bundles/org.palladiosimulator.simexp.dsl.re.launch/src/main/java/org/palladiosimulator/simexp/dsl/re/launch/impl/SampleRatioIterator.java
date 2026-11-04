package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

public class SampleRatioIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> source;
    private final Random random;

    private BigInteger elementsLeft;
    private BigInteger samplesLeft;
    private T nextValue;
    private boolean ready;

    public SampleRatioIterator(Iterator<? extends T> source, BigInteger sourceSize, double percent) {
        if (sourceSize.signum() < 0) {
            throw new IllegalArgumentException("sourceSize must be nonnegative");
        }
        if (!(percent >= 0 && percent <= 100)) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }

        this.random = new Random();
        this.source = Objects.requireNonNull(source);
        this.elementsLeft = sourceSize;
        this.samplesLeft = new BigDecimal(sourceSize).multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
            .toBigIntegerExact();
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
