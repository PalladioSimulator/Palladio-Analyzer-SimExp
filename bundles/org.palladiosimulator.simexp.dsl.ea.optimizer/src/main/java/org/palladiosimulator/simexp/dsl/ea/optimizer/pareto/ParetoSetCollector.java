package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;

public class ParetoSetCollector {
    public static <G extends Gene<?, G>> Collector<EvolutionResult<G, Double>, ?, ISeq<Phenotype<G, Double>>> create(
            double epsilon, IAverageProvider<G> averageProvider) {
        Comparator<Phenotype<G, Double>> dominance = new ParetoDominance<>(averageProvider, s -> Double::compare);

        return Collector.of( //
                () -> new Front<>(dominance) //
                , Front::add //
                , Front::merge //
                , Front::toISeq);
    }
}
