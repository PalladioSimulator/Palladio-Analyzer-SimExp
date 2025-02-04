package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

public class OptimizableChromosomeOHConstraint implements Constraint<BitGene, Vec<double[]>> {

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
        List<Chromosome<BitGene>> chromosomes = new ArrayList<>();
        Genotype<BitGene> genotype = individual.genotype();

        for (int i = 0; i < genotype.length(); i++) {
            if (!genotype.get(i)
                .isValid()) {
                SmodelBitChromosome chromosome = (SmodelBitChromosome) genotype.get(i);
                Chromosome<BitGene> newInstance;
                do {
                    MSeq<BitGene> genes = MSeq.of(chromosome);
                    List<Integer> bitsThatAreSet = new ArrayList<>();

                    for (int j = 0; j < genes.size(); j++) {
                        if (genes.get(j)
                            .bit())
                            bitsThatAreSet.add(j);
                    }
                    if (bitsThatAreSet.size() > 1) {
                        genes = handleToManySetBits(RandomRegistry.random(), genes, bitsThatAreSet);
                    } else if (bitsThatAreSet.size() < 1) {
                        genes = handleMissingBit(RandomRegistry.random(), chromosome.length(), genes);
                    }
                    newInstance = chromosome.newInstance(genes.toISeq());

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

    private MSeq<BitGene> handleToManySetBits(Random random, MSeq<BitGene> genes, List<Integer> bitsThatAreSet) {
        List<Integer> remainingBits = new ArrayList<>(bitsThatAreSet);
        while (remainingBits.size() > 1) {
            int nextInt = random.nextInt(remainingBits.size());

            Integer bitToFlip = remainingBits.get(nextInt);
            remainingBits.remove(nextInt);
            genes.set(bitToFlip, BitGene.of(false));
        }
        return genes;
    }

    private MSeq<BitGene> handleMissingBit(Random random, int length, MSeq<BitGene> genes) {
        genes.set(random.nextInt(length), BitGene.of(true));
        return genes;
    }

}
