package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;

public class ForceValidConstraint<G extends Gene<?, G>> implements Constraint<G, Double> {

    @Override
    public boolean test(Phenotype<G, Double> individual) {
        if (!individual.isValid()) {
            return false;
        }
        return true;
    }

    @Override
    public Phenotype<G, Double> repair(Phenotype<G, Double> individual, long generation) {
        List<Chromosome<G>> chromosomes = new ArrayList<>();
        Genotype<G> genotype = individual.genotype();

        for (int i = 0; i < genotype.length(); i++) {
            Chromosome<G> chromosome = genotype.get(i);
            if (!chromosome.isValid()) {
                Chromosome<G> newChromosome;
                do {
                    newChromosome = chromosome.newInstance();
                } while (!newChromosome.isValid());

                chromosomes.add(newChromosome);
            } else {
                chromosomes.add(chromosome);
            }
        }
        Phenotype<G, Double> repairedIndividual = Phenotype.of(Genotype.of(chromosomes), 0);

        return repairedIndividual;
    }

}
