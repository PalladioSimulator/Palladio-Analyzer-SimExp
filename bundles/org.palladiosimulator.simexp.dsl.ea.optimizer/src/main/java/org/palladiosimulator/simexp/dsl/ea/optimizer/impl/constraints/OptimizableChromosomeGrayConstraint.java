package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;
import io.jenetics.ext.moea.Vec;

//TODO nbruening: Implement usage
public class OptimizableChromosomeGrayConstraint implements Constraint<BitGene, Vec<double[]>> {

    public OptimizableChromosomeGrayConstraint() {
    }

    @Override
    public boolean test(Phenotype<BitGene, Vec<double[]>> individual) {
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
    public Phenotype<BitGene, Vec<double[]>> repair(Phenotype<BitGene, Vec<double[]>> individual, long generation) {
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
        Phenotype<BitGene, Vec<double[]>> repairedIndividual = Phenotype.of(Genotype.of(chromosomes), 0);

        if (!test(repairedIndividual)) {
            throw new RuntimeException("Repaired phenotype is still broken");
        }

        return repairedIndividual;

    }

}
