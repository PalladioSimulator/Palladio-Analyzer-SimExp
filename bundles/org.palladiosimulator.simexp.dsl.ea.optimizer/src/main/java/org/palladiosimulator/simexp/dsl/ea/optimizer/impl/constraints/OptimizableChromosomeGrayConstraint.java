package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;

public class OptimizableChromosomeGrayConstraint implements Constraint<BitGene, Double> {

    public OptimizableChromosomeGrayConstraint() {
    }

    @Override
    public boolean test(Phenotype<BitGene, Double> individual) {
        Genotype<BitGene> genotype = individual.genotype();

        for (int i = 0; i < genotype.length(); i++) {
            if (!genotype.get(i)
                .isValid()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Phenotype<BitGene, Double> repair(Phenotype<BitGene, Double> individual, long generation) {
        List<Chromosome<BitGene>> chromosomes = new ArrayList();
        Genotype<BitGene> genotype = individual.genotype();

        for (int i = 0; i < genotype.length(); i++) {
            if (!genotype.get(i)
                .isValid()) {
                Chromosome<BitGene> chromosome = genotype.get(i);
                Chromosome<BitGene> newInstance;
                do {
                    newInstance = chromosome.newInstance();
                } while (!newInstance.isValid());

                chromosomes.add(newInstance);
            } else {
                chromosomes.add(genotype.get(i));
            }
        }
        Phenotype<BitGene, Double> repairedIndividual = Phenotype.of(Genotype.of(chromosomes), 0);

        if (!test(repairedIndividual)) {
            throw new RuntimeException("Repaired phenotype is still broken");
        }

        return repairedIndividual;

    }

}