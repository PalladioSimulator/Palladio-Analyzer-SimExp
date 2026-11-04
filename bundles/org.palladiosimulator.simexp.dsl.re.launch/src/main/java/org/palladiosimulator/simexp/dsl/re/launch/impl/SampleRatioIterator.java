package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

public class SampleRatioIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> source;
    private final Random random;

    private long elementsLeft;
    private long samplesLeft;
    private T nextValue;
    private boolean ready;

    public SampleRatioIterator(Iterator<? extends T> source, long sourceSize, double percent) {
        if (sourceSize < 0) {
            throw new IllegalArgumentException("sourceSize must be nonnegative");
        }
        if (!(percent >= 0 && percent <= 100)) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }

        this.random = new Random();
        this.source = Objects.requireNonNull(source);
        this.elementsLeft = sourceSize;
        this.samplesLeft = BigDecimal.valueOf(sourceSize)
            .multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
            .longValueExact();
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
