package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
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
        int parallelism = Math.max(Runtime.getRuntime()
            .availableProcessors(), fitnessEvaluator.getParallelism());
        LOGGER.info(String.format("the fitness evaluator has an parallelism of: %d", parallelism));
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
        try {
            return internalOptimize(optimizableProvider, fitnessEvaluator, evolutionStatusReceiver, executor);
        } finally {
            LOGGER.info("shut down executor service");
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
                LOGGER.info("executor service shutt down");
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    EAResult internalOptimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, Executor executor) {
        LOGGER.info("EA running...");

        int overallPower = calculateComplexity(optimizableProvider);
        LOGGER.info(String.format("optimizeable search space: %d", overallPower));

        ////// to phenotype
        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        OptimizableNormalizer normalizer = new OptimizableNormalizer(expressionCalculator);
        Genotype<BitGene> genotype = buildGenotype(optimizableProvider, normalizer);

        ///// setup EA
        EAOptimizationEngineBuilder builder = new EAOptimizationEngineBuilder(config);
        double epsilon = expressionCalculator.getEpsilon();
        final double penaltyForInvalids = config.penaltyForInvalids();
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(epsilon, fitnessEvaluator, normalizer,
                penaltyForInvalids);
        Engine<BitGene, Double> engine = builder.buildEngine(fitnessFunction, genotype, executor);

        //// run optimization
        return new EAOptimizationRunner().runOptimization(evolutionStatusReceiver, normalizer, fitnessFunction, engine,
                config);
    }

    private Genotype<BitGene> buildGenotype(IOptimizableProvider optimizableProvider,
            OptimizableNormalizer normalizer) {
        List<Optimizable> optimizableList = new ArrayList<>();
        optimizableProvider.getOptimizables()
            .forEach(o -> optimizableList.add(o));
        List<SmodelBitChromosome> normalizedOptimizables = normalizer.toNormalized(optimizableList);
        Genotype<BitGene> genotype = Genotype.of(normalizedOptimizables);
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
