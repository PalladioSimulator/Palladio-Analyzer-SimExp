package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executors;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.OptimizableChromosomeOHConstraint;

import io.jenetics.AnyGene;
import io.jenetics.Crossover;
import io.jenetics.Mutator;
import io.jenetics.Selector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;

public class TuneableEAOptimizerOneHotEncoding extends EAOptimizerOneHotEncoding {

    private static final int SELECTOR_TOURNAMENT = (int) (1000 * 0.05);

    private Selector<AnyGene<OptimizableChromosome>, Double> selector;

    private Selector<AnyGene<OptimizableChromosome>, Double> offspringSelector;

    private Mutator mutator;

    private Crossover<AnyGene<OptimizableChromosome>, Double> crossoverOperator;

    public TuneableEAOptimizerOneHotEncoding(int populationSize,
            Selector<AnyGene<OptimizableChromosome>, Double> selector,
            Selector<AnyGene<OptimizableChromosome>, Double> offspringSelector, Mutator mutator,
            Crossover<AnyGene<OptimizableChromosome>, Double> crossoverOperator) {
        this.populationSize = populationSize;
        this.selector = selector;
        this.offspringSelector = offspringSelector;
        this.mutator = mutator;
        this.crossoverOperator = crossoverOperator;
    }

    @Override
    protected Engine<AnyGene<OptimizableChromosome>, Double> buildEngine(int numThreads,
            OptimizableChromosomeFactory chromoCreator,
            Codec<OptimizableChromosome, AnyGene<OptimizableChromosome>> codec) {
        final Engine<AnyGene<OptimizableChromosome>, Double> engine;

        if (numThreads == 1) {
            engine = Engine.builder(chromoCreator::eval, codec)
                .populationSize(100)
                .executor(Runnable::run)
                .constraint(new OptimizableChromosomeOHConstraint())
                .selector(selector)
                .offspringSelector(offspringSelector)
                .alterers(mutator, crossoverOperator)
                .build();
        } else {
            engine = Engine.builder(chromoCreator::eval, codec)
                .populationSize(100)
                .executor(Executors.newFixedThreadPool(numThreads))
                .constraint(new OptimizableChromosomeOHConstraint())
                .selector(selector)
                .offspringSelector(offspringSelector)
                .alterers(mutator, crossoverOperator)
                .build();
        }
        return engine;
    }

}
