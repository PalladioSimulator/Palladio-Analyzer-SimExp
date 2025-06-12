package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Gene;
import io.jenetics.Genotype;

public interface ITranscoder<G extends Gene<?, G>> {
    Genotype<G> toGenotype(List<Optimizable> optimizables);

    List<OptimizableValue<?>> toOptimizableValues(Genotype<G> genotype);
}
