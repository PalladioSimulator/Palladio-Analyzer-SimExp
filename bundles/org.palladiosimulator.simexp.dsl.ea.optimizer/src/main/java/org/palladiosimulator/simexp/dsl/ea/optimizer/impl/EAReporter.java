package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;
import java.util.function.Consumer;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;

public class EAReporter<G extends Gene<?, G>> implements Consumer<EvolutionResult<G, Double>> {
    private final IEAEvolutionStatusReceiver evolutionStatusReceiver;
    private final ITranscoder<G> transcoder;

    public EAReporter(IEAEvolutionStatusReceiver evolutionStatusReceiver, ITranscoder<G> transcoder) {
        this.evolutionStatusReceiver = evolutionStatusReceiver;
        this.transcoder = transcoder;
    }

    @Override
    public void accept(EvolutionResult<G, Double> result) {
        long generation = result.generation();
        Phenotype<G, Double> phenotype = result.bestPhenotype();
        Genotype<G> genotype = phenotype.genotype();
        List<OptimizableValue<?>> optimizables = transcoder.toOptimizableValues(genotype);
        double fitness = result.bestFitness();
        evolutionStatusReceiver.reportStatus(generation, optimizables, fitness);
    }

}