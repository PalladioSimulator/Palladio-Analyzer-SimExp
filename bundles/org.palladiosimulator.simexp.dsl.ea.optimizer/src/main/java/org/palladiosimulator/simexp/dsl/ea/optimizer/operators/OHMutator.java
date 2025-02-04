package org.palladiosimulator.simexp.dsl.ea.optimizer.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.ext.moea.Vec;
import io.jenetics.internal.math.Probabilities;
import io.jenetics.util.ISeq;

public class OHMutator extends Mutator<BitGene, Vec<double[]>> {

    public OHMutator(double p) {
        super(p);
    }

    @Override
    protected MutatorResult<Chromosome<BitGene>> mutate(final Chromosome<BitGene> chromosome, final double p,
            final Random random) {
        final int P = Probabilities.toInt(p);
        int newBit = random.nextInt(chromosome.length());
        final ISeq<BitGene> unsetGenes = chromosome.stream()
            .map(gene -> gene.newInstance(false))
            .collect(ISeq.toISeq());
        List<MutatorResult<BitGene>> result = new ArrayList();
        for (int i = 0; i < chromosome.length(); i++) {
            if (i == newBit) {
                // TODO nbruening: Das is mutate methode rausziehen
                result.add(MutatorResult.of(unsetGenes.get(i)
                    .newInstance(true), 1));
            } else {
                result.add(MutatorResult.of(unsetGenes.get(i)));
            }
        }

        return MutatorResult.of(chromosome.newInstance(ISeq.of(result)
            .map(MutatorResult::result)), result.stream()
                .mapToInt(MutatorResult::mutations)
                .sum());
    }

}
