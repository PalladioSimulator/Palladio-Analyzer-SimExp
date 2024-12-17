package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.Pair;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.SingleOptimizableChromosome;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;

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
        boolean result = true;
        for (SingleOptimizableChromosome chromoPair : allele.chromosomes) {
            Chromosome chromosome = ((Genotype) chromoPair.genotype()).chromosome();

            if (chromoPair.optimizable()
                .getDataType() == DataType.BOOL) {
                // TODO handle this case implicitly
                // do nothing
            } else if (chromosome instanceof BitChromosome bitChromo) {
                if (!bitChromosomeValid(bitChromo)) {
                    result = false;
                }
            } else {
                throw new RuntimeException("Not implemented yet");
            }
        }
        return result;
    }

    @Override
    public Phenotype<AnyGene<OptimizableChromosome>, Double> repair(
            Phenotype<AnyGene<OptimizableChromosome>, Double> individual, long generation) {
        List<Pair> localChromosomes = new ArrayList();
        OptimizableChromosome allele = individual.genotype()
            .chromosome()
            .gene()
            .allele();
        Random random = new Random();

        for (SingleOptimizableChromosome chromoPair : allele.chromosomes) {
            Chromosome chromosome = ((Genotype) chromoPair.genotype()).chromosome();

            if ((chromoPair.optimizable()
                .getDataType() != DataType.BOOL) && (chromosome instanceof BitChromosome bitChromo)) {
                while (!bitChromosomeValid(bitChromo)) {
                    BitSet bitSet = bitChromo.toBitSet();
                    List<Integer> array = extracted(bitSet);

                    bitSet = handleMissingBit(random, bitChromo, bitSet, array);
                    handleToManySetBits(random, bitSet, array);
                    bitChromo = BitChromosome.of(bitSet, bitChromo.length());

                }
                chromoPair.setGenotype(Genotype.of(bitChromo));

            }
        }
        if (!test(individual)) {
            throw new RuntimeException("this should not happen");
        }

        return individual;
    }

    private void handleToManySetBits(Random random, BitSet bitSet, List<Integer> array) {
        while (array.size() > 1) {
            int nextInt = random.nextInt(array.size());

            Integer bitToFlip = array.get(nextInt);
            array.remove(nextInt);
            bitSet.flip(bitToFlip);
        }
    }

    private BitSet handleMissingBit(Random random, BitChromosome bitChromo, BitSet bitSet, List<Integer> array) {
        if (array.size() == 0) {
            int idx = random.nextInt(bitChromo.length());
            bitSet = new BitSet(bitChromo.length());
            bitSet.set(idx);
        }
        return bitSet;
    }

    private List<Integer> extracted(BitSet bitSet) {
        return bitSet.stream()
            .boxed()
            .collect(Collectors.toList());
    }

    private boolean bitChromosomeValid(BitChromosome bitChromo) {
        return bitChromo.bitCount() == 1;
    }

}
