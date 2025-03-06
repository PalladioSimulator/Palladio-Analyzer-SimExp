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
import io.jenetics.ext.moea.Vec;

public class EAReporter implements Consumer<EvolutionResult<BitGene, Vec<double[]>>> {
    private final IEAEvolutionStatusReceiver evolutionStatusReceiver;
    private final OptimizableNormalizer normalizer;

    public EAReporter(IEAEvolutionStatusReceiver evolutionStatusReceiver, OptimizableNormalizer normalizer) {
        this.evolutionStatusReceiver = evolutionStatusReceiver;
        this.normalizer = normalizer;
    }

    @Override
    public void accept(EvolutionResult<BitGene, Vec<double[]>> result) {
        long generation = result.generation();
        Phenotype<BitGene, Vec<double[]>> phenotype = result.bestPhenotype();
        Genotype<BitGene> genotype = phenotype.genotype();
        List<SmodelBitChromosome> chromosomes = genotype.stream()
            .map(g -> g.as(SmodelBitChromosome.class))
            .collect(Collectors.toList());
        List<OptimizableValue<?>> optimizables = normalizer.toOptimizableValues(chromosomes);
        double fitness = phenotype.fitness()
            .data()[0];
        evolutionStatusReceiver.reportStatus(generation, optimizables, fitness);
    }
}