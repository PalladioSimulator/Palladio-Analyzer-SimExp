package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Genotype;
import io.jenetics.engine.Codec;

public class OptimizableRepresentation {
    // Type to Type
    public Codec<?, ?> toGenotype(Optimizable optimizable) {
        return null;

    }

    // Value to Value
    public OptimizableValue<?> toPhenoValue(Genotype<?> chromosomes) {
        return null;

    }
}
