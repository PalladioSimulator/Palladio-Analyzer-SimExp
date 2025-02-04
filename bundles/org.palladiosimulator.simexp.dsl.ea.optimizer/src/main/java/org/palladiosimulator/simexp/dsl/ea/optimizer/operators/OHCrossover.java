package org.palladiosimulator.simexp.dsl.ea.optimizer.operators;

import java.util.Random;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;

import io.jenetics.BitGene;
import io.jenetics.Crossover;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

public class OHCrossover extends Crossover<BitGene, Vec<double[]>> {

    private double inOutProbability;

    public OHCrossover(double probability, double inOutProbability) {
        super(probability);
        this.inOutProbability = inOutProbability;

    }

    @Override
    protected int crossover(MSeq<BitGene> that, MSeq<BitGene> other) {
        int idxThat = findIdxOfBitSet(that);
        int idxOther = findIdxOfBitSet(other);

        Random random = RandomRegistry.random();
        if (random.nextDouble() < inOutProbability) {
            int distance = Math.abs(idxThat - idxOther);
            int firstRandomIdx = random.nextInt(distance);
            int secondRandomIdx = random.nextInt(distance);
            int minIdx = Math.min(idxThat, idxOther);
            int firstNewIdx = minIdx + firstRandomIdx;
            int secondNewIdx = minIdx + secondRandomIdx;

            that.swap(idxThat, firstNewIdx);
            other.swap(idxOther, secondNewIdx);
        } else {
            int distance = Math.abs(idxThat - idxOther);
            int firstRandomIdx = random.nextInt(distance);
            int secondRandomIdx = random.nextInt(distance);
            int firstNewIdx = Math.min(idxThat, idxOther) - firstRandomIdx;
            int secondNewIdx = Math.max(idxThat, idxOther) + secondRandomIdx;
            while (firstNewIdx < 0) {
                firstNewIdx = that.length() - Math.abs(firstNewIdx);
            }
            while (secondNewIdx >= that.length()) {
                secondNewIdx = secondNewIdx - that.length();
            }

            that.swap(idxThat, firstNewIdx);
            other.swap(idxOther, secondNewIdx);
        }

        return 2;
    }

    private int findIdxOfBitSet(MSeq<BitGene> seq) {
        for (int i = 0; i < seq.size(); i++) {
            if (seq.get(i)
                .bit()) {
                return i;
            }

        }
        throw new OptimizableProcessingException("Not a single gene was set: " + seq);
    }

}
