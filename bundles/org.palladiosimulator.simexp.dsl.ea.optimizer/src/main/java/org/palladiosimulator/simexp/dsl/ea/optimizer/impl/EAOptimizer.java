package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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
        List<Optimizable> optimizableList = new ArrayList<>();
        optimizableProvider.getOptimizables()
            .forEach(o -> optimizableList.add(o));
        List<SmodelBitChromosome> normalizedOptimizables = normalizer.toNormalized(optimizableList);
        Genotype<BitGene> genotype = Genotype.of(normalizedOptimizables);
        ////// to phenotype end

        final Engine<BitGene, Double> engine;
        FitnessFunction fitnessFunction = new FitnessFunction(fitnessEvaluator, normalizer);

        engine = Engine.builder(fitnessFunction::apply, genotype)
            .populationSize(100)
            .selector(new TournamentSelector<>((int) (1000 * 0.05)))
            .offspringSelector(new TournamentSelector<>((int) (1000 * 0.05)))
            .alterers(new Mutator<>(0.2), new UniformCrossover<>(0.5))
            .build();
        //// setup EA

        runOptimization(evolutionStatusReceiver, normalizer, engine);
    }

    @Override
    public void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, int numThreads) {
        assert (numThreads > 0);
        LOGGER.info("EA running...");
        ////// to phenotype
        OptimizableNormalizer normalizer = new OptimizableNormalizer(optimizableProvider.getExpressionCalculator());
        List<Optimizable> optimizableList = new ArrayList<>();
        optimizableProvider.getOptimizables()
            .forEach(o -> optimizableList.add(o));
        List<SmodelBitChromosome> normalizedOptimizables = normalizer.toNormalized(optimizableList);
        Genotype<BitGene> genotype = Genotype.of(normalizedOptimizables);
        ////// to phenotype end

        final Engine<BitGene, Double> engine;
        FitnessFunction fitnessFunction = new FitnessFunction(fitnessEvaluator, normalizer);

        if (numThreads == 1) {
            engine = Engine.builder(fitnessFunction::apply, genotype)
                .populationSize(100)
                .executor(Runnable::run)
                .constraint(new OptimizableChromosomeBinaryConstraint())
                .selector(new TournamentSelector<>((int) (1000 * 0.05)))
                .offspringSelector(new TournamentSelector<>((int) (1000 * 0.05)))
                .alterers(new Mutator<>(0.2), new UniformCrossover<>(0.5))
                .build();
        } else {
            engine = Engine.builder(fitnessFunction::apply, genotype)
                .populationSize(100)
                .executor(Executors.newFixedThreadPool(numThreads))
                .constraint(new OptimizableChromosomeBinaryConstraint())
                .selector(new TournamentSelector<>((int) (1000 * 0.05)))
                .offspringSelector(new TournamentSelector<>((int) (1000 * 0.05)))
                .alterers(new Mutator<>(0.2), new UniformCrossover<>(0.5))
                .build();
        }

        //// setup EA
        runOptimization(evolutionStatusReceiver, normalizer, engine);
    }

    private void runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver, OptimizableNormalizer normalizer,
            final Engine<BitGene, Double> engine) {
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        final Phenotype<BitGene, Double> phenotype = engine.stream()
            .limit(bySteadyFitness(7))
            .limit(500)
            .peek(statistics)
            .peek(reporter)
            .collect(EvolutionResult.toBestPhenotype());

        LOGGER.info("EA finished...");

        LOGGER.info(statistics);
    }
}
