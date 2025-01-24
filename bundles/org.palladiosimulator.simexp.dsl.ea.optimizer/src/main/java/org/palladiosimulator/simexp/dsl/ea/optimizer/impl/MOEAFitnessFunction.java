package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.ext.moea.Vec;

public class MOEAFitnessFunction implements Function<Genotype<BitGene>, Vec<double[]>> {

    private static final Logger LOGGER = Logger.getLogger(FitnessFunction.class);

    private final IEAFitnessEvaluator fitnessEvaluator;
    private final OptimizableNormalizer optimizableNormalizer;

    public MOEAFitnessFunction(IEAFitnessEvaluator fitnessEvaluator, OptimizableNormalizer optimizableNormalizer) {
        this.fitnessEvaluator = fitnessEvaluator;
        this.optimizableNormalizer = optimizableNormalizer;
    }

    @Override
    public Vec<double[]> apply(Genotype<BitGene> genotype) {
        List<SmodelBitChromosome> chromosomes = extracted(genotype);
        List<OptimizableValue<?>> optimizableValues = optimizableNormalizer.toOptimizableValues(chromosomes);

        Future<Double> fitnessFuture = fitnessEvaluator.calcFitness(optimizableValues);
        try {
            double fitness = fitnessFuture.get();
            return Vec.of(fitness);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(String.format("%s -> return fitness of 0.0", e.getMessage()), e);
        }

        return Vec.of(0.0);
    }

    private List<SmodelBitChromosome> extracted(Genotype<BitGene> genotype) {
        List<SmodelBitChromosome> chromosomes = new ArrayList<>();
        genotype.forEach(new Consumer<Chromosome<BitGene>>() {

            @Override
            public void accept(Chromosome<BitGene> chromosome) {
                chromosomes.add((SmodelBitChromosome) chromosome);
            }
        });
        return chromosomes;
    }

}
