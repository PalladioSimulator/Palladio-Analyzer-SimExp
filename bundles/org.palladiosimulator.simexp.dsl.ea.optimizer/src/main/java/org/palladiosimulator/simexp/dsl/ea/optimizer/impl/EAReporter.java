package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;
import java.util.function.Consumer;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;

public class EAReporter implements Consumer<EvolutionResult<BitGene, Double>> {
    private final IEAEvolutionStatusReceiver evolutionStatusReceiver;
    private final ITranscoder<BitGene> transcoder;

    public EAReporter(IEAEvolutionStatusReceiver evolutionStatusReceiver, ITranscoder<BitGene> transcoder) {
        this.evolutionStatusReceiver = evolutionStatusReceiver;
        this.transcoder = transcoder;
    }

    @Override
    public void accept(EvolutionResult<BitGene, Double> result) {
        long generation = result.generation();
        Phenotype<BitGene, Double> phenotype = result.bestPhenotype();
        Genotype<BitGene> genotype = phenotype.genotype();
        List<OptimizableValue<?>> optimizables = transcoder.toOptimizableValues(genotype);
        double fitness = result.bestFitness();
        evolutionStatusReceiver.reportStatus(generation, optimizables, fitness);
    }

}