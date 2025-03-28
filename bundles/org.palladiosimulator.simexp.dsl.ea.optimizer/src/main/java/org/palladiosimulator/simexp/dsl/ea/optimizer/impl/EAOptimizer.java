package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;

public class EAOptimizer implements IEAOptimizer {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private IEAConfig config;

    public EAOptimizer(IEAConfig config) {
        this.config = config;
    }

    @Override
    public EAResult optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        return internalOptimize(optimizableProvider, fitnessEvaluator, evolutionStatusReceiver,
                ForkJoinPool.commonPool());

    }

    EAResult optimizeSingleThread(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        return internalOptimize(optimizableProvider, fitnessEvaluator, evolutionStatusReceiver, Runnable::run);

    }

    private EAResult internalOptimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, Executor executor) {
        LOGGER.info("EA running...");

        int overallPower = calculateComplexity(optimizableProvider);
        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        LOGGER.info(String.format("optimizeable search space: %d", overallPower));

        ////// to phenotype
        OptimizableIntegerChromoNormalizer normalizer = new OptimizableIntegerChromoNormalizer(expressionCalculator);
        Genotype<IntegerGene> genotype = buildGenotype(optimizableProvider, normalizer);

        ///// setup EA
        final Engine<IntegerGene, Double> engine;
        MOEAFitnessFunction fitnessFunction;

        double epsilon = expressionCalculator.getEpsilon();
        if (config.penaltyForInvalids()
            .isPresent()) {
            fitnessFunction = new MOEAFitnessFunction(epsilon, fitnessEvaluator, normalizer, config.penaltyForInvalids()
                .get());
        } else {
            fitnessFunction = new MOEAFitnessFunction(epsilon, fitnessEvaluator, normalizer);
        }

        EAOptimizationEngineBuilder builder = new EAOptimizationEngineBuilder(config);

        engine = builder.buildEngine(fitnessFunction, genotype, executor);

        //// run optimization
        return new EAOptimizationRunner().runOptimization(evolutionStatusReceiver, normalizer, fitnessFunction, engine,
                config);
    }

    private Genotype<IntegerGene> buildGenotype(IOptimizableProvider optimizableProvider,
            OptimizableIntegerChromoNormalizer normalizer) {
        List<Optimizable> optimizableList = new ArrayList<>();
        optimizableProvider.getOptimizables()
            .forEach(o -> optimizableList.add(o));
        List<SmodelIntegerChromosome> normalizedOptimizables = normalizer
            .toNormalizedAndRemoveSingleValuedChromosomes(optimizableList);
        Genotype<IntegerGene> genotype = Genotype.of(normalizedOptimizables);
        return genotype;
    }

    private int calculateComplexity(IOptimizableProvider optimizableProvider) {
        Collection<Optimizable> optimizables = optimizableProvider.getOptimizables();
        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        PowerUtil powerUtil = new PowerUtil(expressionCalculator);
        List<Integer> powers = optimizables.stream()
            .map(o -> powerUtil.getPower(o))
            .filter(p -> p > 1)
            .collect(Collectors.toList());
        Integer overallPower = powers.stream()
            .reduce(1, (a, b) -> a * b);
        return overallPower;
    }

}
