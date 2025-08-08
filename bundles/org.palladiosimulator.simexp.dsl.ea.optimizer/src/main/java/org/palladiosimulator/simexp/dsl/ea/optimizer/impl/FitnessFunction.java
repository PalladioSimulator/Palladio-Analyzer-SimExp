package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Gene;
import io.jenetics.Genotype;

public class FitnessFunction<G extends Gene<?, G>>
        implements Function<Genotype<G>, Double>, IEvaluationStatisticsReporter {
    private static final Logger LOGGER = Logger.getLogger(FitnessFunction.class);

    private final IEAFitnessEvaluator fitnessEvaluator;
    private final ITranscoder<G> transcoder;
    private final double epsilon;
    private final double penaltyForInvalids;

    private Set<List<OptimizableValue<?>>> evaluatedOptimizables = Collections.synchronizedSet(new HashSet<>());

    public FitnessFunction(double epsilon, IEAFitnessEvaluator fitnessEvaluator, ITranscoder<G> transcoder,
            double penaltyForInvalids) {
        this.epsilon = epsilon;
        this.fitnessEvaluator = fitnessEvaluator;
        this.transcoder = transcoder;
        this.penaltyForInvalids = round(penaltyForInvalids);
    }

    @Override
    public Double apply(Genotype<G> genotype) {
        if (!genotype.isValid()) {
            return penaltyForInvalids;
        }

        List<OptimizableValue<?>> optimizableValues = transcoder.toOptimizableValues(genotype);
        evaluatedOptimizables.add(optimizableValues);
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

    @Override
    public long getNumberOfUniqueFitnessEvaluations() {
        return evaluatedOptimizables.size();
    }
}
