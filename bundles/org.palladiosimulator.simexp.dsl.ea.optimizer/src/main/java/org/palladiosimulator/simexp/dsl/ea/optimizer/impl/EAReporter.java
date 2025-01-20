package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;

public class EAReporter implements Consumer<EvolutionResult<BitGene, Double>> {
    private final IEAEvolutionStatusReceiver evolutionStatusReceiver;
    private final OptimizableNormalizer normalizer;

    public EAReporter(IEAEvolutionStatusReceiver evolutionStatusReceiver, OptimizableNormalizer normalizer) {
        this.evolutionStatusReceiver = evolutionStatusReceiver;
        this.normalizer = normalizer;
    }

    @Override
    public void accept(EvolutionResult<BitGene, Double> result) {
        long generation = result.generation();
        Phenotype<BitGene, Double> phenotype = result.bestPhenotype();
        Genotype<BitGene> genotype = phenotype.genotype();
        List<SmodelBitChromosome> chromosomes = genotype.stream()
            .map(g -> g.as(SmodelBitChromosome.class))
            .collect(Collectors.toList());
        List<OptimizableValue<?>> optimizables = normalizer.toOptimizableValues(chromosomes);
        Double fitness = result.bestFitness();
        evolutionStatusReceiver.reportStatus(generation, optimizables, fitness);
    }
}