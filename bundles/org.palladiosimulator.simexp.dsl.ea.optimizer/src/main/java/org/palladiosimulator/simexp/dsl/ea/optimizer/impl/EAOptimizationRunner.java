package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class EAOptimizationRunner {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    @SuppressWarnings("unchecked")
    public EAResult runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableNormalizer normalizer, final Engine<BitGene, Vec<double[]>> engine) {
        ParetoCompatibleEvolutionStatistics paretoStatistics = new ParetoCompatibleEvolutionStatistics();

        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        ISeq<Phenotype<BitGene, Vec<double[]>>> result = engine.stream()
            .limit(bySteadyFitness(7))
            .limit(10)
            .peek(reporter)
            .peek(paretoStatistics)
            .collect(MOEA.toParetoSet(IntRange.of(1, 10)));

        LOGGER.info("EA finished...");

        LOGGER.info(paretoStatistics);

        // Print results
        // TODO nbruening: remove
//        result.stream()
//            .forEach(s -> LOGGER.info(s.toString()));

        // all pareto efficient optimizables have the same fitness, so just take
        // the fitness from the first phenotype
        double bestFitness = result.stream()
            .findFirst()
            .get()
            .fitness()
            .data()[0];

        Phenotype<BitGene, Vec<double[]>>[] phenotypes = result.stream()
            .toArray(Phenotype[]::new);

        List<List<OptimizableValue<?>>> paretoFront = new ArrayList<>();
        for (Phenotype<BitGene, Vec<double[]>> currentPheno : phenotypes) {
            List<SmodelBitChromosome> chromosomes = new ArrayList<>();
            for (int i = 0; i < currentPheno.genotype()
                .length(); i++) {
                SmodelBitChromosome currentChromosome = currentPheno.genotype()
                    .get(i)
                    .as(SmodelBitChromosome.class);
                chromosomes.add(currentChromosome);
            }
            paretoFront.add(normalizer.toOptimizableValues(chromosomes));
        }

        return new EAResult(bestFitness, paretoFront);
    }

}
