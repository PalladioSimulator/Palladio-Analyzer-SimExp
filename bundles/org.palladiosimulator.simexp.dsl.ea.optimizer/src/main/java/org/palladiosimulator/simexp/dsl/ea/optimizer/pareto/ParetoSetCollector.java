package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.moea.ElementComparator;
import io.jenetics.ext.moea.ElementDistance;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class ParetoSetCollector {

    public static Collector<EvolutionResult<BitGene, Double>, ?, ISeq<Phenotype<BitGene, Double>>> create() {

        Comparator<Double> dominance = (Double a, Double b) -> Vec.of(a)
            .dominance(Vec.of(b));
        ElementComparator<Double> elementComparator = (Double a, Double b, int index) -> a.compareTo(b);
        ElementDistance<Double> distance = (Double a, Double b, int index) -> Vec.of(a)
            .distance(Vec.of(b), index);
        ToIntFunction<Double> dimension = (Double value) -> 1;

        return MOEA.toParetoSet(IntRange.of(3, 10), dominance, elementComparator, distance, dimension);

    }

}
