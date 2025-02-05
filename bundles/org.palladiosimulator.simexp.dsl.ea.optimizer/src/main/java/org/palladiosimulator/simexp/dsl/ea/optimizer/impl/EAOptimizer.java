package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.BitInterpreter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.OneHotBitInterpreter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.Vec;

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
        BitInterpreter bitInterpreter = new OneHotBitInterpreter();
        OptimizableNormalizer normalizer = new OptimizableNormalizer(optimizableProvider.getExpressionCalculator(),
                bitInterpreter);
        Genotype<BitGene> genotype = buildGenotype(optimizableProvider, normalizer);

        ///// setup EA
        final Engine<BitGene, Vec<double[]>> engine;
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(fitnessEvaluator, normalizer);
        EAOptimizationEngineBuilder builder = new EAOptimizationEngineBuilder();
        engine = builder.buildEngine(fitnessFunction, genotype, bitInterpreter, 100, executor, 5, 5, 0.2, 0.05);

        //// run optimization
        return new EAOptimizationRunner().runOptimization(evolutionStatusReceiver, normalizer, engine);
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

}
