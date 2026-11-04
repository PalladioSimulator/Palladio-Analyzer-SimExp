package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Optional;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class SampleRatioIterator<T> extends BaseSampleIterator<T> {
    public SampleRatioIterator(Optional<ISeedProvider> seedProvider, Iterator<? extends T> source,
            BigInteger sourceSize, double percent) {
        super(seedProvider, source);
        if (sourceSize.signum() < 0) {
            throw new IllegalArgumentException("sourceSize must be nonnegative");
        }
        if (!(percent >= 0 && percent <= 100)) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }

        BigInteger samplesLeft = new BigDecimal(sourceSize).multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
            .toBigIntegerExact();
        setStartValues(sourceSize, samplesLeft);
    }
}
