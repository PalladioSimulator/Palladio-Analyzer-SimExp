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
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;

public class MOEAFitnessFunction implements Function<Genotype<IntegerGene>, Double> {

    private static final Logger LOGGER = Logger.getLogger(MOEAFitnessFunction.class);

    private Set<List<OptimizableValue<?>>> synchronizedSetOfEvaluatedOptimizable = Collections
        .synchronizedSet(new HashSet<>());

    private final IEAFitnessEvaluator fitnessEvaluator;

    private final OptimizableIntegerChromoNormalizer optimizableNormalizer;
    private final double epsilon;

    private double penaltyForInvalids = 0.0;

    public MOEAFitnessFunction(double epsilon, IEAFitnessEvaluator fitnessEvaluator,
            OptimizableIntegerChromoNormalizer optimizableNormalizer) {
        this.epsilon = epsilon;
        this.fitnessEvaluator = fitnessEvaluator;
        this.optimizableNormalizer = optimizableNormalizer;
    }

    public MOEAFitnessFunction(double epsilon, IEAFitnessEvaluator fitnessEvaluator,
            OptimizableIntegerChromoNormalizer optimizableNormalizer, double penaltyForInvalids) {
        this(epsilon, fitnessEvaluator, optimizableNormalizer);
        this.penaltyForInvalids = penaltyForInvalids;
    }

    @Override
    public Double apply(Genotype<IntegerGene> genotype) {
        List<SmodelIntegerChromosome> chromosomes = extracted(genotype);
        for (SmodelIntegerChromosome currentChromo : chromosomes) {
            if (!currentChromo.isValid()) {
                return round(penaltyForInvalids);
            }
        }
        List<OptimizableValue<?>> optimizableValues = optimizableNormalizer.toOptimizableValues(chromosomes);
        synchronizedSetOfEvaluatedOptimizable.add(optimizableValues);
        Future<Optional<Double>> fitnessFuture = fitnessEvaluator.calcFitness(optimizableValues);
        try {
            Optional<Double> optionalFitness = fitnessFuture.get();

            double fitness = optionalFitness.isPresent() ? optionalFitness.get() : penaltyForInvalids;
            return round(fitness);
        } catch (ExecutionException | InterruptedException e) {
            Double roundedPenalty = round(penaltyForInvalids);
            LOGGER.error(String.format("%s -> return penalty fitness of " + roundedPenalty, e.getMessage()), e);
            return roundedPenalty;
        }
    }

    private Double round(final Double number) {
        double multiplicator = (long) (1.0 / epsilon);
        return Math.round(number * multiplicator) / multiplicator;
    }

    public long getNumberOfUniqueFitnessEvaluations() {
        return synchronizedSetOfEvaluatedOptimizable.size();
    }

    private List<SmodelIntegerChromosome> extracted(Genotype<IntegerGene> genotype) {
        List<SmodelIntegerChromosome> chromosomes = new ArrayList<>();
        genotype.forEach(new Consumer<Chromosome<IntegerGene>>() {

            @Override
            public void accept(Chromosome<IntegerGene> chromosome) {
                chromosomes.add((SmodelIntegerChromosome) chromosome);
            }
        });
        return chromosomes;
    }

}
