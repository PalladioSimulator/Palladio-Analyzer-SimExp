package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.function.Function;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Gene;
import io.jenetics.Genotype;

public class SingleChromosome<T extends Gene<?, T>> {

    private Function<Genotype<T>, ?> function;
    private Genotype<T> genotype;
    private Optimizable optimizable;

    public SingleChromosome(Function<Genotype<T>, ?> function, Genotype<T> genotype, Optimizable optimizable) {
        this.function = function;
        this.genotype = genotype;
        this.optimizable = optimizable;
    }

    public Object getPhenotype() {
        return function.apply(genotype);
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
}
