package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.math.BigInteger;
import java.text.DecimalFormat;
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
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.ForceValidConstraint;
import org.palladiosimulator.simexp.dsl.ea.optimizer.moea.MOEASetCollector;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.OptimizableIntNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Crossover;
import io.jenetics.EliteSelector;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Engine.Builder;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
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
        ITranscoder<IntegerGene> transcoder = new OptimizableIntNormalizer(expressionCalculator);
        return doOptimize(transcoder, optimizableProvider, fitnessEvaluator, evolutionStatusReceiver, executor);
    }

    private <G extends Gene<?, G>> EAResult doOptimize(ITranscoder<G> transcoder,
            IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, Executor executor) {
        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        Collection<Optimizable> optimizables = optimizableProvider.getOptimizables();
        PowerUtil powerUtil = new PowerUtil(expressionCalculator);
        BigInteger overallPower = powerUtil.calculateComplexity(optimizables);
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        LOGGER.info(String.format("optimizeable search space: %s", df.format(overallPower)));

        // Setup EA
        Genotype<G> genotype = buildGenotype(optimizables, transcoder);
        double epsilon = expressionCalculator.getEpsilon();
        final double penaltyForInvalids = config.penaltyForInvalids();
        FitnessFunction<G> fitnessFunction = new FitnessFunction<>(epsilon, fitnessEvaluator, transcoder,
                penaltyForInvalids);
        Engine<G, Double> engine = buildEngine(fitnessFunction, genotype, executor);
        EvaluationStatistics<G> evaluationStatistics = new EvaluationStatistics<>(fitnessFunction, overallPower);

        // Run EA
        return runOptimization(evaluationStatistics, evolutionStatusReceiver, transcoder, fitnessFunction, engine);
    }

    private <G extends Gene<?, G>> Genotype<G> buildGenotype(Collection<Optimizable> optimizables,
            ITranscoder<G> normalizer) {
        List<Optimizable> optimizableList = new ArrayList<>(optimizables);
        Genotype<G> genotype = normalizer.toGenotype(optimizableList);
        return genotype;
    }

    private <G extends Gene<?, G>> Engine<G, Double> buildEngine(FitnessFunction<G> fitnessFunction,
            Genotype<G> genotype, Executor executor) {
        Factory<Genotype<G>> constraintFactory = new ForceValidConstraint<G>().constrain(genotype);
        Builder<G, Double> builder = Engine.builder(fitnessFunction::apply, constraintFactory)
            .populationSize(config.populationSize())
            .executor(executor)
            .survivorsSelector(new EliteSelector<G, Double>(new TournamentSelector<>(config.survivorTournamentSize())))
            .offspringSelector(new TournamentSelector<>(config.offspringTournamentSize()));

        builder = addAlterers(builder);

        return builder.build();
    }

    private <G extends Gene<?, G>> Builder<G, Double> addAlterers(Builder<G, Double> builder) {
        Mutator<G, Double> mutator = new Mutator<>(config.mutationRate());
        Crossover<G, Double> crossover = new UniformCrossover<>(config.crossoverRate());
        return builder.alterers(crossover, mutator);
    }

    private <G extends Gene<?, G>> EAResult runOptimization(EvaluationStatistics<G> evaluationStatistics,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, ITranscoder<G> normalizer,
            FitnessFunction<G> fitnessFunction, final Engine<G, Double> engine) {
        LOGGER.info("EA running...");

        EvolutionStream<G, Double> evolutionStream = engine.stream();
        evolutionStream = addTerminationConditions(evolutionStream, config);

        EAReporter<G> reporter = new EAReporter<>(evolutionStatusReceiver, normalizer);
        EvolutionStatistics<Double, DoubleMomentStatistics> standardStatistics = EvolutionStatistics.ofNumber();
        Stream<EvolutionResult<G, Double>> resultStream = evolutionStream.peek(reporter)
            .peek(standardStatistics);

        Optional<ISeedProvider> seedProvider = config.getSeedProvider();
        final EvolutionResult<G, Double> result;
        if (seedProvider.isEmpty()) {
            result = resultStream.collect(EvolutionResult.toBestEvolutionResult());
        } else {
            long seed = seedProvider.get()
                .getLong();
            result = RandomRegistry.with(new Random(seed),
                    r -> resultStream.collect(EvolutionResult.toBestEvolutionResult()));
        }
        final double bestFitness = result.bestFitness();
        LOGGER.info("EA finished");
        StringBuilder resultStatistics = new StringBuilder();
        resultStatistics.append(standardStatistics.toString());
        resultStatistics.append("\n");
        resultStatistics.append(evaluationStatistics);
        LOGGER.info(resultStatistics.toString());

        List<List<OptimizableValue<?>>> equivalentOptimizableValues = buildMOEAList(normalizer, result);

        return new EAResult(bestFitness, equivalentOptimizableValues);
    }

    private <G extends Gene<?, G>> List<List<OptimizableValue<?>>> buildMOEAList(ITranscoder<G> normalizer,
            EvolutionResult<G, Double> result) {
        Collector<EvolutionResult<G, Double>, ?, ISeq<Phenotype<G, Double>>> moeaCollector = MOEASetCollector.create();
        final ISeq<Phenotype<G, Double>> phenotypes = Stream.of(result)
            .collect(moeaCollector);
        List<List<OptimizableValue<?>>> moeaFront = phenotypes.stream()
            .map(p -> normalizer.toOptimizableValues(p.genotype()))
            .toList();
        return moeaFront;
    }

    private <G extends Gene<?, G>> EvolutionStream<G, Double> addTerminationConditions(
            EvolutionStream<G, Double> evolutionStream, IEAConfig config) {
        if (config.maxGenerations()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byFixedGeneration(config.maxGenerations()
                .get()));
        }
        if (config.steadyFitness()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.bySteadyFitness(config.steadyFitness()
                .get()));
        }
        return evolutionStream;
    }
}
