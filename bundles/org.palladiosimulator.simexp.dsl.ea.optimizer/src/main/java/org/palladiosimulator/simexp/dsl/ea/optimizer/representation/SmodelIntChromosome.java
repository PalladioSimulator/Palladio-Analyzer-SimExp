package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static io.jenetics.util.RandomRegistry.random;
import static java.lang.String.format;

import java.util.Random;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.internal.math.Randoms;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

public class SmodelIntChromosome extends IntegerChromosome {
    private static final long serialVersionUID = 1L;

    private final Optimizable optimizable;

    private SmodelIntChromosome(Optimizable optimizable, ISeq<IntegerGene> genes, IntRange lengthRange) {
        super(genes, lengthRange);
        this.optimizable = optimizable;
    }

    public Optimizable getOptimizable() {
        return optimizable;
    }

    @Override
    public SmodelIntChromosome newInstance(ISeq<IntegerGene> genes) {
        return new SmodelIntChromosome(optimizable, genes, lengthRange());
    }

    @Override
    public SmodelIntChromosome newInstance() {
        return of(optimizable, min(), max(), lengthRange());
    }

    public static SmodelIntChromosome of(Optimizable optimizable, int min, int max) {
        return of(optimizable, min, max, IntRange.of(1));
    }

    private static SmodelIntChromosome of(Optimizable optimizable, int min, int max, IntRange lengthRange) {
        final ISeq<IntegerGene> values = seq(min, max, lengthRange);
        return new SmodelIntChromosome(optimizable, values, lengthRange);
    }

    private static ISeq<IntegerGene> seq(int min, int max, IntRange lengthRange) {
        final Random r = random();
        return MSeq.<IntegerGene> ofLength(Randoms.nextInt(lengthRange, r))
            .fill(() -> IntegerGene.of(nextInt(r, min, max), min, max))
            .toISeq();
    }

    private static int nextInt(final Random random, final int min, final int max) {
        if (min > max) {
            throw new IllegalArgumentException(format("Min >= max: %d >= %d", min, max));
        }

        final int diff = max - min + 1;
        int result = 0;

        if (diff <= 0) {
            do {
                result = random.nextInt();
            } while (result < min || result > max);
        } else {
            result = random.nextInt(diff) + min;
        }

        return result;
    }
}
