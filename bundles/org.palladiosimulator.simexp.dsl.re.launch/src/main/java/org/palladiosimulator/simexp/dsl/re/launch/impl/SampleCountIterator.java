package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigInteger;
import java.util.Iterator;

public class SampleCountIterator<T> extends BaseSampleIterator<T> {
    public SampleCountIterator(Iterator<? extends T> source, BigInteger sourceSize, BigInteger sampleSize) {
        super(source);
        if (sourceSize.signum() < 0 || sampleSize.signum() < 0) {
            throw new IllegalArgumentException("Sizes must be nonnegative");
        }
        BigInteger samplesLeft = sourceSize.min(sampleSize);
        setStartValues(sourceSize, samplesLeft);
    }

}
