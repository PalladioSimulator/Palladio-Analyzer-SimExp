package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IFitnessResultIdentificator;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;

public class EAReporter<G extends Gene<?, G>> implements Consumer<EvolutionResult<G, Double>> {
    private final IEAEvolutionStatusReceiver evolutionStatusReceiver;
    private final ITranscoder<G> transcoder;
    private final IFitnessResultIdentificator fitnessResultIdentificator;

    public EAReporter(IEAEvolutionStatusReceiver evolutionStatusReceiver, ITranscoder<G> transcoder,
            IFitnessResultIdentificator fitnessResultIdentificator) {
        this.evolutionStatusReceiver = evolutionStatusReceiver;
        this.transcoder = transcoder;
        this.fitnessResultIdentificator = fitnessResultIdentificator;
    }

    @Override
    public void accept(EvolutionResult<G, Double> result) {
        long generation = result.generation();
        Phenotype<G, Double> phenotype = result.bestPhenotype();
        Genotype<G> genotype = phenotype.genotype();
        List<OptimizableValue<?>> optimizables = transcoder.toOptimizableValues(genotype);
        double fitness = result.bestFitness();
        List<IndividualResult> population = result.population()
            .stream()
            .map(p -> buildIndividualResult(p))
            .toList();
        evolutionStatusReceiver.reportStatus(generation, optimizables, fitness, population);
    }

    private IndividualResult buildIndividualResult(Phenotype<G, Double> phenotype) {
        List<OptimizableValue<?>> optimizableValues = transcoder.toOptimizableValues(phenotype.genotype());
        Optional<String> identificator = fitnessResultIdentificator.getIdentificator(optimizableValues);
        return new IndividualResult(phenotype.fitness(), optimizableValues, identificator.orElse("n/a"));
    }

}