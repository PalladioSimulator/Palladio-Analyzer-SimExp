package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private static final Logger LOGGER = Logger.getLogger(MOEAFitnessFunction.class);

    private Set<List<OptimizableValue<?>>> synchronizedSetOfEvaluatedOptimizable = Collections
        .synchronizedSet(new HashSet<>());

    private final IEAFitnessEvaluator fitnessEvaluator;
    private final OptimizableNormalizer optimizableNormalizer;

    private double penaltyForInvalids = 0.0;

    public MOEAFitnessFunction(IEAFitnessEvaluator fitnessEvaluator, OptimizableNormalizer optimizableNormalizer) {
        this.fitnessEvaluator = fitnessEvaluator;
        this.optimizableNormalizer = optimizableNormalizer;
    }

    public MOEAFitnessFunction(IEAFitnessEvaluator fitnessEvaluator, OptimizableNormalizer optimizableNormalizer,
            double penaltyForInvalids) {
        this(fitnessEvaluator, optimizableNormalizer);
        this.penaltyForInvalids = penaltyForInvalids;
    }

    @Override
    public Vec<double[]> apply(Genotype<BitGene> genotype) {
        List<SmodelBitChromosome> chromosomes = extracted(genotype);
        for (SmodelBitChromosome currentChromo : chromosomes) {
            if (!currentChromo.isValid()) {
                return Vec.of(penaltyForInvalids);
            }
        }
        List<OptimizableValue<?>> optimizableValues = optimizableNormalizer.toOptimizableValues(chromosomes);
        synchronizedSetOfEvaluatedOptimizable.add(optimizableValues);
        Future<Optional<Double>> fitnessFuture = fitnessEvaluator.calcFitness(optimizableValues);
        try {
            Optional<Double> optionalFitness = fitnessFuture.get();

            double fitness = optionalFitness.isPresent() ? optionalFitness.get() : penaltyForInvalids;
            return Vec.of(fitness);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(String.format("%s -> return penalty fitness of " + penaltyForInvalids, e.getMessage()), e);
            return Vec.of(penaltyForInvalids);
        }
    }

    public long getNumberOfUniqueFitnessEvaluations() {
        return synchronizedSetOfEvaluatedOptimizable.size();
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
