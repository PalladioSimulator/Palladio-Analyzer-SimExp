package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.function.Function;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Genotype;

public class SingleOptimizableChromosome {

    private Function function;
    private Genotype<?> genotype;
    private Optimizable optimizable;

    public SingleOptimizableChromosome(Function function, Genotype<?> genotype, Optimizable optimizable) {
        this.function = function;
        this.genotype = genotype;
        this.optimizable = optimizable;
    }

    public Function function() {
        return function;
    }

    public Genotype<?> genotype() {
        return genotype;
    }

    public void setGenotype(Genotype<?> second) {
        this.genotype = second;
    }

    public Optimizable optimizable() {
        return optimizable;
    }
}
