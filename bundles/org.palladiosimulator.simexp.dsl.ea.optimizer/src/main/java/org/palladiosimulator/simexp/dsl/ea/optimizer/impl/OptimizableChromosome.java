package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;

public class OptimizableChromosome {

    public List<SingleChromosome> chromosomes;

    private IEAFitnessEvaluator fitnessEvaluator;

    public OptimizableChromosome(List<SingleChromosome> chromosomes, List<CodecOptimizablePair> declaredChromoSubTypes,
            IEAFitnessEvaluator fitnessEvaluator) {
        this.chromosomes = chromosomes;
        this.fitnessEvaluator = fitnessEvaluator;
    }

    public IEAFitnessEvaluator getFitnessEvaluator() {
        return fitnessEvaluator;
    }

}