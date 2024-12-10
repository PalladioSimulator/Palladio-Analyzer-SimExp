package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;

public class EAOptimizer implements IEAOptimizer {
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private BoundsParser parser = new BoundsParser();

    @Override
    public void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        LOGGER.info("EA running...");
        List<CodecOptimizablePair> parsedCodecs = new ArrayList<>();

        for (Optimizable currentOpt : optimizableProvider.getOptimizables()) {
            DataType dataType = currentOpt.getDataType();
            Bounds optValue = currentOpt.getValues();
            parsedCodecs.add(new CodecOptimizablePair(
                    parser.parseBounds(optValue, optimizableProvider.getExpressionCalculator(), dataType), currentOpt));
        }

        OptimizableChromosomeFactory chromoCreator = new OptimizableChromosomeFactory();
        Codec<OptimizableChromosome, AnyGene<OptimizableChromosome>> codec = Codec.of(
                Genotype.of(AnyChromosome.of(chromoCreator.getNextChromosomeSupplier(parsedCodecs, fitnessEvaluator))),
                gt -> gt.gene()
                    .allele());

        final Engine<AnyGene<OptimizableChromosome>, Double> engine = Engine.builder(chromoCreator::eval, codec)
            .populationSize(500)
            .constraint(new OptimizableChromosomeConstraint())
            .selector(new TournamentSelector<>(5))
            .offspringSelector(new TournamentSelector<>(5))
            .alterers(new SwapMutator<>(0.2), new SinglePointCrossover<>(0.1))
            .build();

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<AnyGene<OptimizableChromosome>, Double> phenotype = engine.stream()
            .limit(bySteadyFitness(200))
            .limit(500)
            .peek(statistics)
            .collect(EvolutionResult.toBestPhenotype());

        LOGGER.info("EA finished...");

        OptimizableChromosome phenoChromo = phenotype.genotype()
            .chromosome()
            .gene()
            .allele();

        LOGGER.info("PhenoChromo: " + phenoChromo.chromosomes.get(0)
            .genotype() + " " + chromoCreator.eval(phenoChromo));

        List<OptimizableValue<?>> finalOptimizableValues = new ArrayList();

        for (SingleChromosome singleChromo : phenoChromo.chromosomes) {
            LOGGER.info(singleChromo.function()
                .apply(singleChromo.genotype()));
            finalOptimizableValues
                .add(new IEAFitnessEvaluator.OptimizableValue(singleChromo.optimizable(), singleChromo.function()
                    .apply(singleChromo.genotype())));
        }

        evolutionStatusReceiver.reportStatus(finalOptimizableValues, phenotype.fitness());

        LOGGER.info(statistics);
    }

}
