package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.io.IOException;
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

public class EAFitnessFunction implements Function<Genotype<BitGene>, Double> {

    private static final Logger LOGGER = Logger.getLogger(EAFitnessFunction.class);

    private Set<Genotype> synchronizedSetOfEvaluatedOptimizable = Collections.synchronizedSet(new HashSet<>());

    private final IEAFitnessEvaluator fitnessEvaluator;
    private final OptimizableNormalizer optimizableNormalizer;
    private final double epsilon;

    private final double penaltyForInvalids;

    public EAFitnessFunction(double epsilon, IEAFitnessEvaluator fitnessEvaluator,
            OptimizableNormalizer optimizableNormalizer, double penaltyForInvalids) {
        this.epsilon = epsilon;
        this.fitnessEvaluator = fitnessEvaluator;
        this.optimizableNormalizer = optimizableNormalizer;
        this.penaltyForInvalids = penaltyForInvalids;
    }

    @Override
    public Double apply(Genotype<BitGene> genotype) {
        synchronizedSetOfEvaluatedOptimizable.add(genotype);
        List<SmodelBitChromosome> chromosomes = extracted(genotype);
        for (SmodelBitChromosome currentChromo : chromosomes) {
            if (!currentChromo.isValid()) {
                return round(penaltyForInvalids);
            }
        }

        List<OptimizableValue<?>> optimizableValues = optimizableNormalizer.toOptimizableValues(chromosomes);
        try {
            Future<Optional<Double>> fitnessFuture = fitnessEvaluator.calcFitness(optimizableValues);
            Optional<Double> optionalFitness = fitnessFuture.get();

            double fitness = optionalFitness.isPresent() ? optionalFitness.get() : penaltyForInvalids;
            return round(fitness);
        } catch (ExecutionException | InterruptedException | IOException e) {
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
