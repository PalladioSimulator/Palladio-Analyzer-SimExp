package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.function.Function;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Gene;
import io.jenetics.Genotype;

public class SingleOptimizableChromosome<T extends Gene<?, T>> {

    private Function<Genotype<T>, ?> decoder;
    private Genotype<T> genotype;
    private Optimizable optimizable;

    public SingleOptimizableChromosome(Function<Genotype<T>, ?> decoder, Genotype<T> genotype,
            Optimizable optimizable) {
        this.decoder = decoder;
        this.genotype = genotype;
        this.optimizable = optimizable;
    }

    public Object getPhenotype() {
        return decoder.apply(genotype);
    }

    public Genotype<?> genotype() {
        return genotype;
    }

    public void setGenotype(Genotype<T> second) {
        this.genotype = second;
    }

    public Optimizable optimizable() {
        return optimizable;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OptimizableValue<?> toOptimizableValue() {
        return new IEAFitnessEvaluator.OptimizableValue(optimizable(), getPhenotype());
    }
}
