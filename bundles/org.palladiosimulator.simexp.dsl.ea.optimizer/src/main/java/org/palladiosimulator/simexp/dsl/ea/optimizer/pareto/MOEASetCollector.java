package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.moea.ElementComparator;
import io.jenetics.ext.moea.ElementDistance;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class MOEASetCollector {

    static final int SIZE_MIN = 3;
    static final int SIZE_MAX = 10;

    public static <G extends Gene<?, G>> Collector<EvolutionResult<G, Double>, ?, ISeq<Phenotype<G, Double>>> create(
            double epsilon) {
        Comparator<Double> dominance = (Double a, Double b) -> compareWithPrecision(a, b, epsilon);
        ElementComparator<Double> elementComparator = (Double a, Double b, int index) -> compareWithPrecision(a, b,
                epsilon);
        ElementDistance<Double> distance = (Double a, Double b, int index) -> Vec.of(a)
            .distance(Vec.of(b), index);
        ToIntFunction<Double> dimension = (Double value) -> 1;

        return MOEA.toParetoSet(IntRange.of(SIZE_MIN, SIZE_MAX), dominance, elementComparator, distance, dimension);
    }

    private static int compareWithPrecision(Double a, Double b, double epsilon) {
        if (almostEqualAbs(a, b, epsilon)) {
            return 0;
        }
        return Double.compare(a, b);
    }

    private static boolean almostEqualAbs(double a, double b, double eps) {
        if (Double.isNaN(a) || Double.isNaN(b)) {
            return false;
        }
        if (Double.isInfinite(a) || Double.isInfinite(b)) {
            return a == b;
        }
        return Math.abs(a - b) <= eps;
    }
}
