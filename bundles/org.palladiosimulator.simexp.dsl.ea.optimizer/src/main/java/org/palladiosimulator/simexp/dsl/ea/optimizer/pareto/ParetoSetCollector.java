package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.moea.ElementComparator;
import io.jenetics.ext.moea.ElementDistance;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class ParetoSetCollector {
    static final int SIZE_MIN = 3;
    static final int SIZE_MAX = 10;

    public static <G extends Gene<?, G>> Collector<EvolutionResult<G, Double>, ?, ISeq<Phenotype<G, Double>>> create(
            double epsilon) {
        Comparator<Phenotype<G, Double>> dominance = (Phenotype<G, Double> a, Phenotype<G, Double> b) -> a.fitness()
            .compareTo(b.fitness());
        ElementComparator<Phenotype<G, Double>> comparator = (Phenotype<G, Double> a, Phenotype<G, Double> b,
                int index) -> a.fitness()
                    .compareTo(b.fitness());
        ElementDistance<Phenotype<G, Double>> distance = (Phenotype<G, Double> a, Phenotype<G, Double> b,
                int index) -> Vec.of(a.fitness())
                    .distance(Vec.of(b.fitness()), index);
        ToIntFunction<Phenotype<G, Double>> dimension = (Phenotype<G, Double> value) -> 1;
        IntRange size = IntRange.of(SIZE_MIN, SIZE_MAX);

        return Collector.of( //
                () -> new Front<>(size, dominance, comparator, distance, dimension) //
                , Front::add //
                , Front::merge //
                , Front::toISeq);
    }
}
