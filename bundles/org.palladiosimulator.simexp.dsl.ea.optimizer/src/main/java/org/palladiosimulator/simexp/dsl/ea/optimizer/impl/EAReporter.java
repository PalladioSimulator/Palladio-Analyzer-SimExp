package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;

public class EAReporter implements Consumer<EvolutionResult<IntegerGene, Double>> {
    private final IEAEvolutionStatusReceiver evolutionStatusReceiver;
    private final OptimizableIntegerChromoNormalizer normalizer;

    public EAReporter(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableIntegerChromoNormalizer normalizer) {
        this.evolutionStatusReceiver = evolutionStatusReceiver;
        this.normalizer = normalizer;
    }

    // remove
    @Override
    public void accept(EvolutionResult<IntegerGene, Double> result) {
        long generation = result.generation();

        Phenotype<IntegerGene, Double> phenotype = result.bestPhenotype();
        Genotype<IntegerGene> genotype = phenotype.genotype();
        List<SmodelIntegerChromosome> chromosomes = genotype.stream()
            .map(g -> g.as(SmodelIntegerChromosome.class))
            .collect(Collectors.toList());
        List<OptimizableValue<?>> optimizables = normalizer.toOptimizableValues(chromosomes);
        double fitness = result.bestFitness();
        evolutionStatusReceiver.reportStatus(generation, optimizables, fitness);
    }

}