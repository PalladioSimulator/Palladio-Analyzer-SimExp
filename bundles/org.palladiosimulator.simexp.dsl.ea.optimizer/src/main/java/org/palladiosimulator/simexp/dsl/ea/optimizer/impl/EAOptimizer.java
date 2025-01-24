package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.RunInMainThreadEAConfig;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class EAOptimizer implements IEAOptimizer {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private IEAConfig config;

    public EAOptimizer(IEAConfig config) {
        this.config = config;

    }

    @Override
    public EAResult optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        if (config instanceof RunInMainThreadEAConfig) {
            return internalOptimize(optimizableProvider, fitnessEvaluator, evolutionStatusReceiver, Runnable::run);
        } else {
            return internalOptimize(optimizableProvider, fitnessEvaluator, evolutionStatusReceiver,
                    ForkJoinPool.commonPool());
        }
    }

    private EAResult internalOptimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver, Executor executor) {
        LOGGER.info("EA running...");
        ////// to phenotype
        OptimizableNormalizer normalizer = new OptimizableNormalizer(optimizableProvider.getExpressionCalculator());
        Genotype<BitGene> genotype = buildGenotype(optimizableProvider, normalizer);

        ///// setup EA
        final Engine<BitGene, Vec<double[]>> engine;
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(fitnessEvaluator, normalizer);
        OptimizationEngineBuilder builder = new OptimizationEngineBuilder();
        engine = builder.buildEngine(fitnessFunction, genotype, 100, executor, 5, 5, 0.2, 0.3);

        //// run optimization
        return runOptimization(evolutionStatusReceiver, normalizer, engine);
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

    private EAResult runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableNormalizer normalizer, final Engine<BitGene, Vec<double[]>> engine) {
//        final EvolutionStatistics<Vec<Double[]>, ?> statistics = EvolutionStatistics.ofNumber();
        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

//        engine.stream()
//            .limit(bySteadyFitness(7))
//            .limit(500)
//            .peek(statistics)
//            .peek(reporter)
//            .collect(MOEA.toParetoSet(IntRange.of(30, 50)));

        ISeq<Phenotype<BitGene, Vec<double[]>>> result = engine.stream()
            .limit(bySteadyFitness(7))
            .limit(100)
            .collect(MOEA.toParetoSet(IntRange.of(1, 10)));

        LOGGER.info("EA finished...");

        result.stream()
            .forEach(s -> System.out.println(s.toString()));
        Optional<Phenotype<BitGene, Vec<double[]>>> bestPhenoOptional = result.stream()
            .max(Comparator.comparing(Phenotype::fitness));

        Phenotype<BitGene, Vec<double[]>> bestPhenotype = bestPhenoOptional.get();
//        
//        Phenotype<BitGene, Double> bestPhenotype = result.bestPhenotype();
//        Genotype<BitGene> genotype = bestPhenotype.genotype();
//        List<SmodelBitChromosome> bestChromosomes = genotype.stream()
//            .map(g -> g.as(SmodelBitChromosome.class))
//            .collect(Collectors.toList());
//        List<OptimizableValue<?>> bestOptimizables = normalizer.toOptimizableValues(bestChromosomes);
//
//        LOGGER.info(statistics);

        return new EAResult(bestPhenotype.fitness()
            .data()[0], List.of(
                    normalizer.toOptimizable((SmodelBitChromosome) bestPhenotype.genotype()
                        .chromosome())));
    }
}
