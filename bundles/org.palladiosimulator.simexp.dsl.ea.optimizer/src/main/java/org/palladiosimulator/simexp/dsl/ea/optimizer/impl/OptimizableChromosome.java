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

public class OptimizableChromosome {

    public List<SingleChromosome> chromosomes;

    private IEAFitnessEvaluator fitnessEvaluator;

    public OptimizableChromosome(List<SingleChromosome> chromosomes, List<CodecOptimizablePair> declaredChromoSubTypes,
            IEAFitnessEvaluator fitnessEvaluator) {
        this.chromosomes = chromosomes;
        this.fitnessEvaluator = fitnessEvaluator;
    }

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    @SuppressWarnings("unchecked")
    public static OptimizableChromosome nextChromosome(List<CodecOptimizablePair> declaredChromoSubTypes,
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

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    public static Supplier<OptimizableChromosome> getNextChromosomeSupplier(List<CodecOptimizablePair> classes,
            IEAFitnessEvaluator fitnessEvaluator) {
        List<CodecOptimizablePair> declaredChromoSubTypes = new ArrayList<>();
        declaredChromoSubTypes.addAll(classes);

        return () -> nextChromosome(declaredChromoSubTypes, fitnessEvaluator);
    }

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    public static double eval(final OptimizableChromosome c) {

        List<OptimizableValue<?>> optimizableValues = new ArrayList();

        for (SingleChromosome currentChromo : c.chromosomes) {
            optimizableValues.add(new IEAFitnessEvaluator.OptimizableValue<Pair>(currentChromo.optimizable(),
                    new DecoderEncodingPair(currentChromo.function(), currentChromo.genotype())));
        }

        Future<Double> calcFitness = c.fitnessEvaluator.calcFitness(optimizableValues);

        try {
            return calcFitness.get(600000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // review-finding-2024-12-09: wrong exception handling; return new
            // RuntimeException/custom OptimizableException instead of 0;
            // do not print stacktrace;
            e.printStackTrace();
            return 0;
            // TODO Throw a runtime exception
        }

    }

}