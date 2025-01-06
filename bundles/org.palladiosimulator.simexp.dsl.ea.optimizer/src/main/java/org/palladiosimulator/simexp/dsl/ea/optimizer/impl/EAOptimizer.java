package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.OptimizableChromosomeBinaryConstraint;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;

public class EAOptimizer implements IEAOptimizer {
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    public static int eval(final Genotype<IntegerGene> gt) {
        return gt.chromosome()
            .gene()
            .intValue();
    }

    @Override
    public void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        LOGGER.info("EA running...");
        ////// to phenotype
        OptimizableNormalizer normalizer = new OptimizableNormalizer(optimizableProvider.getExpressionCalculator());
        Genotype<BitGene> genotype = buildGenotype(optimizableProvider, normalizer);
        ////// to phenotype end
        FitnessFunction fitnessFunction = new FitnessFunction(fitnessEvaluator, normalizer);

        //// setup EA
        final Engine<BitGene, Double> engine = buildEngine(ForkJoinPool.commonPool(), genotype, fitnessEvaluator,
                normalizer);

        runOptimization(evolutionStatusReceiver, normalizer, engine);
    }

    @Override
    public void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, int numThreads) {
        assert (numThreads > 0);
        LOGGER.info("EA running...");
        ////// to phenotype
        OptimizableNormalizer normalizer = new OptimizableNormalizer(optimizableProvider.getExpressionCalculator());
        Genotype<BitGene> genotype = buildGenotype(optimizableProvider, normalizer);
        ////// to phenotype end

        final Engine<BitGene, Double> engine;
        FitnessFunction fitnessFunction = new FitnessFunction(fitnessEvaluator, normalizer);

        if (numThreads == 1) {
            engine = buildEngine(Runnable::run, genotype, fitnessEvaluator, normalizer);
        } else {
            engine = buildEngine(Executors.newFixedThreadPool(numThreads), genotype, fitnessEvaluator, normalizer);
        }

        //// setup EA
        runOptimization(evolutionStatusReceiver, normalizer, engine);
    }

    private Engine<BitGene, Double> buildEngine(Executor executor, Genotype<BitGene> genotype,
            IEAFitnessEvaluator fitnessEvaluator, OptimizableNormalizer normalizer) {
        FitnessFunction fitnessFunction = new FitnessFunction(fitnessEvaluator, normalizer);

        return Engine.builder(fitnessFunction::apply, genotype)
            .populationSize(100)
            .executor(executor)
            .constraint(new OptimizableChromosomeBinaryConstraint())
            .selector(new TournamentSelector<>((int) (1000 * 0.05)))
            .offspringSelector(new TournamentSelector<>((int) (1000 * 0.05)))
            .alterers(new Mutator<>(0.2), new UniformCrossover<>(0.5))
            .build();
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

    private void runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver, OptimizableNormalizer normalizer,
            final Engine<BitGene, Double> engine) {
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<BitGene, Double> phenotype = engine.stream()
            .limit(bySteadyFitness(7))
            .limit(500)
            .peek(statistics)
            .peek(result -> evolutionStatusReceiver.reportStatus(List.of(normalizer.toOptimizable(result.bestPhenotype()
                .genotype()
                .chromosome()
                .as(SmodelBitChromosome.class))), result.bestFitness()))
            .collect(EvolutionResult.toBestPhenotype());

        LOGGER.info("EA finished...");

        LOGGER.info(statistics);
    }
}
