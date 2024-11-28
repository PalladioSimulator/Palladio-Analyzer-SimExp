package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.jenetics.AnyGene;
import io.jenetics.BitChromosome;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;

public class OptimizableChromosomeConstraint implements Constraint<AnyGene<OptimizableChromosome>, Double> {

    @Override
    public boolean test(Phenotype<AnyGene<OptimizableChromosome>, Double> individual) {
        OptimizableChromosome allele = individual.genotype()
            .chromosome()
            .gene()
            .allele();
        for (Triple chromoPair : allele.chromosomes) {
            Chromosome chromosome = ((Genotype) chromoPair.second()).chromosome();

            if (chromosome instanceof BitChromosome bitChromo) {
                return bitChromosomeValid(bitChromo);
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public Phenotype<AnyGene<OptimizableChromosome>, Double> repair(
            Phenotype<AnyGene<OptimizableChromosome>, Double> individual, long generation) {
        List<Pair> localChromosomes = new ArrayList();
        OptimizableChromosome allele = individual.genotype()
            .chromosome()
            .gene()
            .allele();
        for (Triple chromoPair : allele.chromosomes) {
            Chromosome chromosome = ((Genotype) chromoPair.second()).chromosome();

            if (chromosome instanceof BitChromosome bitChromo) {
                while (!bitChromosomeValid(bitChromo)) {
                    BitSet bitSet = bitChromo.toBitSet();
                    Random random = new Random();

                    List<Integer> array = extracted(bitSet);

                    while (array.size() > 1) {
                        int nextInt = random.nextInt(array.size());

                        Integer bitToFlip = array.get(nextInt);
                        array.remove(nextInt);
                        bitSet.flip(bitToFlip);
                    }
                    bitChromo = BitChromosome.of(bitSet, bitSet.length());

                }
                chromoPair.setSecond(Genotype.of(bitChromo));

            } else {
                localChromosomes.add(new Pair(chromoPair.first(), chromoPair.second()));
            }
        }
        if (!test(individual)) {
            throw new RuntimeException("this should not happen");
        }

        return individual;
    }

    private List<Integer> extracted(BitSet bitSet) {
        return bitSet.stream()
            .boxed()
            .collect(Collectors.toList());

//        int i = 0;
//        List<Integer> setBits = new ArrayList();
//        bitSet.stream().toArray();
//        int setIdx = bitSet.nextSetBit(i);
//        while (setIdx != -1) {
//            setBits.add(setIdx);
//            i = setIdx + 1;
//            setIdx = bitSet.nextSetBit(i);
//        }
//        return setBits;
    }

    private boolean bitChromosomeValid(BitChromosome bitChromo) {
        return bitChromo.bitCount() == 1;
    }

}
