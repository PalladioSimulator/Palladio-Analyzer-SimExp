package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static io.jenetics.ext.moea.Pareto.front;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.moea.ParetoFront;
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

    private static class Front<G extends Gene<?, G>, C extends Comparable<? super C>> {
        private final Comparator<Phenotype<G, C>> dominance;

        private Optimize _optimize;
        private ParetoFront<Phenotype<G, C>> _front;

        public Front(Comparator<Phenotype<G, C>> dominance) {
            this.dominance = dominance;
        }

        void add(EvolutionResult<G, C> result) {
            if (_front == null) {
                _optimize = result.optimize();
                _front = new ParetoFront<>(this::dominance, this::equals);
            }

            final ISeq<Phenotype<G, C>> front = front(result.population(), this::dominance);
            _front.addAll(front.asList());
        }

        private int dominance(Phenotype<G, C> a, Phenotype<G, C> b) {
            return _optimize == Optimize.MAXIMUM ? dominance.compare(a, b) : dominance.compare(b, a);
        }

        private boolean equals(Phenotype<?, ?> a, Phenotype<?, ?> b) {
            return Objects.equals(a.genotype(), b.genotype());
        }

        Front<G, C> merge(Front<G, C> front) {
            _front.merge(front._front);
            return this;
        }

        ISeq<Phenotype<G, C>> toISeq() {
            return _front != null ? _front.toISeq() : ISeq.empty();
        }
    }
}
