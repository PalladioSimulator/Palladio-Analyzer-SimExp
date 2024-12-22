package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

public class OptimizerEngineFactory {

//    public static synchronized <G extends Gene<?, G>, C extends Comparable<? super C>> Engine build(
//            Function<? super Genotype<G>, ? extends C> fitnessFunction, Factory<Genotype<G>> genotypeFactory) {
//        return (new OptimizerEngineFactory()).buildEngine(fitnessFunction, genotypeFactory);
//    }

//    public static <G extends Gene<?, G>, C extends Comparable<? super C>> Engine<G, ? extends C> builder(
//            final Function<? super Genotype<G>, ? extends C> fitnessFunction,
//            final Factory<Genotype<G>> genotypeFactory) {
//        return Engine.builder(fitnessFunction, genotypeFactory)
//            .populationSize(100)
//            .selector(new TournamentSelector<>((int) (1000 * 0.05)))
//            .offspringSelector(new TournamentSelector<>((int) (1000 * 0.05)))
//            .alterers(new Mutator<>(0.2), new UniformCrossover<>(0.5))
//            .build();
//
//    }
//
//    public <OptimizableChromosome, AnyGene<OptimizableChromosome>, Double> Builder<AnyGene<OptimizableChromosome>, Double> bla(
//            Function<? super OptimizableChromosome, ? extends Double> ff,
//            Function<? super OptimizableChromosome, ? extends Double> gtf) {
//        return Engine.builder(ff, gtf)
//            .populationSize(100)
//            .selector(new TournamentSelector<>((int) (1000 * 0.05)))
//            .offspringSelector(new TournamentSelector<>((int) (1000 * 0.05)))
//            .alterers(new Mutator<>(0.2), new UniformCrossover<>(0.5))
//            .build();
//
//    }

}
