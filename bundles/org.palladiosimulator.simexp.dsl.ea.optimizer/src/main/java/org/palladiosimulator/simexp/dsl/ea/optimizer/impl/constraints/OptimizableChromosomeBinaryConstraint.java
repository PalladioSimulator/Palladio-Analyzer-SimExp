package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;

public class OptimizableChromosomeBinaryConstraint implements Constraint<BitGene, Double> {

    @Override
    public boolean test(Phenotype<BitGene, Double> individual) {
        if (!individual.isValid()) {
            return false;
        }
        return true;
    }

    @Override
    public Phenotype<BitGene, Double> repair(Phenotype<BitGene, Double> individual, long generation) {
        List<Chromosome<BitGene>> chromosomes = new ArrayList<>();
        Genotype<BitGene> genotype = individual.genotype();

        for (int i = 0; i < genotype.length(); i++) {
            Chromosome<BitGene> chromosome = genotype.get(i);
            if (!chromosome.isValid()) {
                Chromosome<BitGene> newChromosome;
                do {
                    newChromosome = chromosome.newInstance();
                } while (!newChromosome.isValid());

                chromosomes.add(newChromosome);
            } else {
                chromosomes.add(chromosome);
            }
        }
        Phenotype<BitGene, Double> repairedIndividual = Phenotype.of(Genotype.of(chromosomes), 0);

        return repairedIndividual;
    }

}
