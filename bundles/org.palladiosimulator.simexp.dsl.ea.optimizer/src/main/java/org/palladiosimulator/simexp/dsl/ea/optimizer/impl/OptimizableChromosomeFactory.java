package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;

public class OptimizableChromosomeFactory {

    @SuppressWarnings("unchecked")
    private OptimizableChromosome nextChromosome(List<CodecOptimizablePair> declaredChromoSubTypes,
            IEAFitnessEvaluator fitnessEvaluator) {
        List<SingleChromosome> localChromosomes = new ArrayList();
        for (CodecOptimizablePair c : declaredChromoSubTypes) {

            localChromosomes.add(new SingleChromosome(c.first()
                .decoder(),
                    c.first()
                        .encoding()
                        .newInstance(),
                    c.second()));
        }

        return new OptimizableChromosome(localChromosomes, declaredChromoSubTypes, fitnessEvaluator);
    }

    public Supplier<OptimizableChromosome> getNextChromosomeSupplier(List<CodecOptimizablePair> classes,
            IEAFitnessEvaluator fitnessEvaluator) {
        List<CodecOptimizablePair> declaredChromoSubTypes = new ArrayList<>();
        declaredChromoSubTypes.addAll(classes);

        return () -> nextChromosome(declaredChromoSubTypes, fitnessEvaluator);
    }

    public double eval(final OptimizableChromosome c) {

        List<OptimizableValue<?>> optimizableValues = new ArrayList();

        for (SingleChromosome currentChromo : c.chromosomes) {
            optimizableValues.add(new IEAFitnessEvaluator.OptimizableValue<>(currentChromo.optimizable(),
                    currentChromo.getPhenotype()));
        }

        Future<Double> calcFitness = c.getFitnessEvaluator()
            .calcFitness(optimizableValues);

        try {
            return calcFitness.get(600000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            FitnessEvaluationException fitnessEvaluationException = new FitnessEvaluationException(
                    "An exception has been thrown" + " while evaluating the fitness of an individual");
            fitnessEvaluationException.initCause(e);
            throw fitnessEvaluationException;
        }

    }

}