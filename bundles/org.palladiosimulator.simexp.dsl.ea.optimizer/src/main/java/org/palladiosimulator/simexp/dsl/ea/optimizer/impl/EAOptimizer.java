package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.ForceValidConstraint;
import org.palladiosimulator.simexp.dsl.ea.optimizer.pareto.MOEAFitnessFunction;
import org.palladiosimulator.simexp.dsl.ea.optimizer.pareto.ParetoEvolutionStatistics;
import org.palladiosimulator.simexp.dsl.ea.optimizer.pareto.ParetoSetCollector;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.OptimizableBitNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Crossover;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Engine.Builder;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class EAOptimizer implements IEAOptimizer {
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private final IEAConfig config;

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
        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        PowerUtil powerUtil = new PowerUtil(expressionCalculator);
        Collection<Optimizable> optimizables = optimizableProvider.getOptimizables();
        int overallPower = powerUtil.calculateComplexity(optimizables);
        LOGGER.info(String.format("optimizeable search space: %d", overallPower));

        // To genotype
        OptimizableBitNormalizer normalizer = new OptimizableBitNormalizer(expressionCalculator);
        Genotype<BitGene> genotype = buildGenotype(optimizables, normalizer);

        // Setup EA
        double epsilon = expressionCalculator.getEpsilon();
        final double penaltyForInvalids = config.penaltyForInvalids();
        MOEAFitnessFunction<BitGene> fitnessFunction = new MOEAFitnessFunction<>(epsilon, fitnessEvaluator, normalizer,
                penaltyForInvalids);
        Engine<BitGene, Double> engine = buildEngine(fitnessFunction, genotype, executor);

        // Run EA
        return runOptimization(overallPower, evolutionStatusReceiver, normalizer, fitnessFunction, engine);
    }

    private Genotype<BitGene> buildGenotype(Collection<Optimizable> optimizables, ITranscoder<BitGene> normalizer) {
        List<Optimizable> optimizableList = new ArrayList<>(optimizables);
        Genotype<BitGene> genotype = normalizer.toGenotype(optimizableList);
        return genotype;
    }

    private Engine<BitGene, Double> buildEngine(MOEAFitnessFunction<BitGene> fitnessFunction,
            Genotype<BitGene> genotype, Executor executor) {
        Factory<Genotype<BitGene>> constraintFactory = new ForceValidConstraint<BitGene>().constrain(genotype);
        Builder<BitGene, Double> builder = Engine.builder(fitnessFunction::apply, constraintFactory)
            .populationSize(config.populationSize())
            .executor(executor)
            .survivorsSelector(new TournamentSelector<>(SimulationConstants.DEFAULT_SURVIVOR_TOURNAMENT_SIZE))
            .offspringSelector(new TournamentSelector<>(SimulationConstants.DEFAULT_OFFSPRING_TOURNAMENT_SIZE));

        builder = addAlterers(builder);

        return builder.build();
    }

    private Builder<BitGene, Double> addAlterers(Builder<BitGene, Double> builder) {
        Mutator<BitGene, Double> mutator = new Mutator<>(config.mutationRate());
        Crossover<BitGene, Double> crossover = new UniformCrossover<>(config.crossoverRate());
        return builder.alterers(mutator, crossover);
    }

    private EAResult runOptimization(long overallPower, IEAEvolutionStatusReceiver evolutionStatusReceiver,
            ITranscoder<BitGene> normalizer, MOEAFitnessFunction<BitGene> fitnessFunction,
            final Engine<BitGene, Double> engine) {
        LOGGER.info("EA running...");
        ParetoEvolutionStatistics<BitGene> paretoStatistics = new ParetoEvolutionStatistics<>(fitnessFunction,
                overallPower);

        EAReporter<BitGene> reporter = new EAReporter<>(evolutionStatusReceiver, normalizer);

        EvolutionStream<BitGene, Double> evolutionStream = addDerivedTerminationCriterion(engine, config);
        evolutionStream = addDirectTerminationCriterion(config, evolutionStream);

        Stream<EvolutionResult<BitGene, Double>> effectiveStream = evolutionStream.peek(reporter)
            .peek(paretoStatistics);

        final ISeq<Phenotype<BitGene, Double>> result;
        Collector<EvolutionResult<BitGene, Double>, ?, ISeq<Phenotype<BitGene, Double>>> paretoCollector = ParetoSetCollector
            .create();
        Optional<ISeedProvider> seedProvider = config.getSeedProvider();
        if (seedProvider.isEmpty()) {
            result = effectiveStream.collect(paretoCollector);
        } else {
            long seed = seedProvider.get()
                .getLong();
            result = RandomRegistry.with(new Random(seed), r -> effectiveStream.collect(paretoCollector));
        }

        LOGGER.info("EA finished");
        LOGGER.info(paretoStatistics);

        // all pareto efficient optimizables have the same fitness, so just take
        // the fitness from the first phenotype
        double bestFitness = result.stream()
            .findFirst()
            .get()
            .fitness();
        List<List<OptimizableValue<?>>> paretoFront = result.stream()
            .map(p -> normalizer.toOptimizableValues(p.genotype()))
            .toList();

        return new EAResult(bestFitness, paretoFront);
    }

    private EvolutionStream<BitGene, Double> addDirectTerminationCriterion(IEAConfig config,
            EvolutionStream<BitGene, Double> evolutionStream) {
        if (config.maxGenerations()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byFixedGeneration(config.maxGenerations()
                .get()));
        }
        return evolutionStream;
    }

    private EvolutionStream<BitGene, Double> addDerivedTerminationCriterion(final Engine<BitGene, Double> engine,
            IEAConfig config) {
        EvolutionStream<BitGene, Double> evolutionStream = engine.stream();
        if (config.steadyFitness()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.bySteadyFitness(config.steadyFitness()
                .get()));
        }
        return evolutionStream;
    }
}
