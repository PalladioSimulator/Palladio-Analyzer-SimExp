package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Optional;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class SampleCountIterator<T> extends BaseSampleIterator<T> {
    public SampleCountIterator(Optional<ISeedProvider> seedProvider, Iterator<? extends T> source,
            BigInteger sourceSize, BigInteger sampleSize) {
        super(seedProvider, source);
        if (sourceSize.signum() < 0 || sampleSize.signum() < 0) {
            throw new IllegalArgumentException("Sizes must be nonnegative");
        }
        BigInteger samplesLeft = sourceSize.min(sampleSize);
        setStartValues(sourceSize, samplesLeft);
    }

}
