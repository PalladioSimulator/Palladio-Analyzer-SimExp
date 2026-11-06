package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class CartesianSampleIterator implements Iterator<List<OptimizableValue<?>>> {
    private static final BigInteger MAX_TRACKED = BigInteger.valueOf(10_000_000);

    private final List<List<OptimizableValue<?>>> dims; // dims.get(i) = converted values of
                                                        // Optimizable i
    private final BigInteger[] sizes;
    private final BigInteger total;
    private final boolean complement;
    private final SortedSet<BigInteger> tracked; // picked indices, or excluded indices
    private final Iterator<BigInteger> pickedIt; // direct mode only

    private BigInteger cursor = BigInteger.ZERO; // complement mode only
    private BigInteger remaining;

    private CartesianSampleIterator(Optional<ISeedProvider> seedProvider, List<List<OptimizableValue<?>>> dims,
            BigInteger k) {
        this.dims = dims;
        this.sizes = dims.stream()
            .map(d -> BigInteger.valueOf(d.size()))
            .toArray(BigInteger[]::new);
        this.total = productOfSizes(dims);

        if (k.signum() < 0 || k.compareTo(total) > 0) {
            throw new IllegalArgumentException("sample size must be in [0, " + total + "]");
        }
        this.remaining = k;
        BigInteger rest = total.subtract(k);
        this.complement = k.compareTo(rest) > 0;
        BigInteger toTrack = complement ? rest : k;
        if (toTrack.compareTo(MAX_TRACKED) > 0) {
            throw new IllegalArgumentException("sample too large to track: both n and N-n exceed " + MAX_TRACKED);
        }

        RandomGeneratorFactory<RandomGenerator> rngFactory = RandomGeneratorFactory.of("L64X128MixRandom");
        final RandomGenerator rng;
        if (seedProvider.isEmpty()) {
            rng = rngFactory.create();
        } else {
            long longSeed = seedProvider.get()
                .getLong();
            rng = rngFactory.create(longSeed);
        }

        this.tracked = new TreeSet<>();
        while (tracked.size() < toTrack.intValueExact()) {
            tracked.add(randomBelow(total, rng));
        }
        this.pickedIt = complement ? null : tracked.iterator();
    }

    public static CartesianSampleIterator ofCount(Optional<ISeedProvider> seedProvider,
            List<List<OptimizableValue<?>>> dims, BigInteger n) {
        return new CartesianSampleIterator(seedProvider, dims, n);
    }

    public static CartesianSampleIterator ofRatio(Optional<ISeedProvider> seedProvider,
            List<List<OptimizableValue<?>>> dims, double percent) {
        if (!(percent >= 0 && percent <= 100)) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }
        BigInteger total = productOfSizes(dims);
        BigInteger k = new BigDecimal(total).multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
            .toBigIntegerExact();
        return new CartesianSampleIterator(seedProvider, dims, k);
    }

    @Override
    public boolean hasNext() {
        return remaining.signum() > 0;
    }

    @Override
    public List<OptimizableValue<?>> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        BigInteger index;
        if (complement) {
            while (tracked.contains(cursor)) {
                cursor = cursor.add(BigInteger.ONE);
            }
            index = cursor;
            cursor = cursor.add(BigInteger.ONE);
        } else {
            index = pickedIt.next();
        }
        remaining = remaining.subtract(BigInteger.ONE);
        return decode(index);
    }

    /** Last Optimizable varies fastest, matching nested-loop order. */
    private List<OptimizableValue<?>> decode(BigInteger index) {
        List<OptimizableValue<?>> result = new ArrayList<>(Collections.nCopies(dims.size(), null));
        for (int i = dims.size() - 1; i >= 0; i--) {
            BigInteger[] qr = index.divideAndRemainder(sizes[i]);
            result.set(i, dims.get(i)
                .get(qr[1].intValueExact()));
            index = qr[0];
        }
        return result;
    }

    private static BigInteger productOfSizes(List<List<OptimizableValue<?>>> dims) {
        BigInteger p = BigInteger.ONE;
        for (List<?> d : dims) {
            p = p.multiply(BigInteger.valueOf(d.size()));
        }
        return p;
    }

    private BigInteger randomBelow(BigInteger bound, RandomGenerator rng) {
        int bits = bound.bitLength();
        int bytes = (bits + 7) / 8;
        int mask = 0xFF >>> (8 * bytes - bits);
        byte[] buf = new byte[bytes];
        BigInteger v;
        do {
            rng.nextBytes(buf);
            buf[0] &= (byte) mask;
            v = new BigInteger(1, buf);
        } while (v.compareTo(bound) >= 0);
        return v;
    }
}
