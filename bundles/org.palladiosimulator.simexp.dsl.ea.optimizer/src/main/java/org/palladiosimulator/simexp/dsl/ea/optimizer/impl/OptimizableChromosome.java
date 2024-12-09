package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;

import io.jenetics.Chromosome;

public class OptimizableChromosome {

    public List<SingleChromosome> chromosomes;

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    private static final List<CodecOptimizablePair> declaredChromoSubTypes = new ArrayList();

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    private static IEAFitnessEvaluator fitnessEvaluator;

    public OptimizableChromosome(Map<Class, Chromosome> mapClass2Chromo, List<SingleChromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    @SuppressWarnings("unchecked")
    public static OptimizableChromosome nextChromosome() {
        Map<Class, Chromosome> mapClass2Chromo = new HashMap();
        List<SingleChromosome> localChromosomes = new ArrayList();
        for (CodecOptimizablePair c : declaredChromoSubTypes) {

            localChromosomes.add(new SingleChromosome(c.first()
                .decoder(),
                    c.first()
                        .encoding()
                        .newInstance(),
                    c.second()));
        }

        return new OptimizableChromosome(mapClass2Chromo, localChromosomes);
    }

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    public static Supplier<OptimizableChromosome> getNextChromosomeSupplier(List<CodecOptimizablePair> classes,
            IEAFitnessEvaluator fitnessEvaluator) {
        declaredChromoSubTypes.addAll(classes);
        OptimizableChromosome.fitnessEvaluator = fitnessEvaluator;

        return () -> nextChromosome();
    }

    // review-finding-2024-12-09: remove 'static'; not thread-safe
    public static double eval(final OptimizableChromosome c) {

        double value = 0;
        List<OptimizableValue<?>> optimizableValues = new ArrayList();

        for (SingleChromosome currentChromo : c.chromosomes) {
            optimizableValues.add(new IEAFitnessEvaluator.OptimizableValue<Pair>(currentChromo.optimizable(),
                    new DecoderEncodingPair(currentChromo.function(), currentChromo.genotype())));
        }

        Future<Double> calcFitness = fitnessEvaluator.calcFitness(optimizableValues);

        try {
            return calcFitness.get(600000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // review-finding-2024-12-09: wrong exception handling; return new
            // RuntimeException/custom OptimizableException instead of 0;
            // do not print stacktrace;
            e.printStackTrace();
            return 0;
        }

    }

}