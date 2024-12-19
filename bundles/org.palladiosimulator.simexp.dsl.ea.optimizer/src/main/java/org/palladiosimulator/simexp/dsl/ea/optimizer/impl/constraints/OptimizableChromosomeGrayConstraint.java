package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import java.util.BitSet;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.SingleOptimizableChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.GrayConverter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;

import io.jenetics.AnyGene;
import io.jenetics.BitChromosome;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;
import io.jenetics.util.RandomRegistry;

public class OptimizableChromosomeGrayConstraint implements Constraint<AnyGene<OptimizableChromosome>, Double> {

    private GrayConverter grayConverterHelper;

    public OptimizableChromosomeGrayConstraint() {
        grayConverterHelper = new GrayConverter();
    }

    @Override
    public boolean test(Phenotype<AnyGene<OptimizableChromosome>, Double> individual) {
        OptimizableChromosome allele = individual.genotype()
            .chromosome()
            .gene()
            .allele();
        boolean result = true;
        for (SingleOptimizableChromosome chromoPair : allele.chromosomes) {
            Chromosome chromosome = chromoPair.genotype()
                .chromosome();

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

        OptimizableChromosome allele = individual.genotype()
            .chromosome()
            .gene()
            .allele();
        boolean result = true;
        for (SingleOptimizableChromosome chromoPair : allele.chromosomes) {
            Chromosome chromosome = chromoPair.genotype()
                .chromosome();

            if (chromoPair.optimizable()
                .getDataType() == DataType.BOOL) {
                // TODO handle this case implicitly
                // do nothing
            } else if (chromosome instanceof BitChromosome bitChromo) {
                while (!bitChromosomeValid(bitChromo)) {
                    int newIdx = RandomRegistry.random()
                        .nextInt(0, (int) Math.pow(2, bitChromo.length()));
                    BitSet newBitSet = grayConverterHelper.idxToGray(newIdx, bitChromo.length());
                    bitChromo = BitChromosome.of(newBitSet, bitChromo.length());
                }
                chromoPair.setGenotype(Genotype.of(bitChromo));
            } else {
                throw new RuntimeException("Not implemented yet");
            }
        }
        if (!test(individual)) {
            throw new RuntimeException("this should not happen");
        }

        return individual;
    }

    private boolean bitChromosomeValid(BitChromosome bitChromo) {
        int idx = grayConverterHelper.grayToIdx(bitChromo.toBitSet());
        return (idx < Math.pow(2, bitChromo.length())) && (idx >= 0);
    }

}
