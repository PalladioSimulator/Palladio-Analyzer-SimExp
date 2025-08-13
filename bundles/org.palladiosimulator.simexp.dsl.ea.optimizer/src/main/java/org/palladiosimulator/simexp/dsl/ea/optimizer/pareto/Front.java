package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static io.jenetics.ext.moea.Pareto.front;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToIntFunction;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.moea.ElementComparator;
import io.jenetics.ext.moea.ElementDistance;
import io.jenetics.ext.moea.ParetoFront;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class Front<G extends Gene<?, G>, C extends Comparable<? super C>> {
    private final IntRange _size;
    private final Comparator<Phenotype<G, C>> _dominance;
    private final ElementComparator<Phenotype<G, C>> _comparator;
    private final ElementDistance<Phenotype<G, C>> _distance;
    private final ToIntFunction<Phenotype<G, C>> _dimension;

    private Optimize _optimize;
    private ParetoFront<Phenotype<G, C>> _front;

    Front(IntRange size, Comparator<Phenotype<G, C>> dominance, ElementComparator<Phenotype<G, C>> comparator,
            ElementDistance<Phenotype<G, C>> distance, ToIntFunction<Phenotype<G, C>> dimension) {
        _size = size;
        _dominance = dominance;
        _comparator = comparator;
        _distance = distance;
        _dimension = dimension;
    }

    void add(EvolutionResult<G, C> result) {
        if (_front == null) {
            _optimize = result.optimize();
            _front = new ParetoFront<>(this::dominance, this::equals);
        }

        final ISeq<Phenotype<G, C>> front = front(result.population(), this::dominance);
        _front.addAll(front.asList());
        trim();
    }

    private int dominance(Phenotype<G, C> a, Phenotype<G, C> b) {
        return _optimize == Optimize.MAXIMUM ? _dominance.compare(a, b) : _dominance.compare(b, a);
    }

    private boolean equals(Phenotype<?, ?> a, Phenotype<?, ?> b) {
        return Objects.equals(a.genotype(), b.genotype());
    }

    private void trim() {
        assert _front != null;
        assert _optimize != null;

        if (_front.size() > _size.max() - 1) {
            _front.trim(_size.min(), this::compare, _distance, v -> _dimension.applyAsInt(v));
        }
    }

    private int compare(Phenotype<G, C> a, Phenotype<G, C> b, int i) {
        return _optimize == Optimize.MAXIMUM ? _comparator.compare(a, b, i) : _comparator.compare(b, a, i);
    }

    Front<G, C> merge(Front<G, C> front) {
        _front.merge(front._front);
        trim();
        return this;
    }

    ISeq<Phenotype<G, C>> toISeq() {
        return _front != null ? _front.toISeq() : ISeq.empty();
    }

}