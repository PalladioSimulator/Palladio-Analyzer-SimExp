package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class SmodelIntegerChromosome extends IntegerChromosome {

    private Optimizable optimizable;

    private int numOfValues;

    protected SmodelIntegerChromosome(ISeq<IntegerGene> genes, IntRange lengthRange, Optimizable optimizable,
            int numOfValues) {
        super(genes, lengthRange);
        this.optimizable = optimizable;
        this.numOfValues = numOfValues;
    }

    public Optimizable getOptimizable() {
        return optimizable;
    }

    public int getNumOfValues() {
        return numOfValues;
    }

    public static SmodelIntegerChromosome of(IntRange intRange, Optimizable optimizable) {
        // TODO nbruening: intRange selbst parsen und nicht erst Integer Chromosom erstellen?
        IntegerChromosome integerChromosome = IntegerChromosome.of(intRange);
        List<IntegerGene> genes = new ArrayList();
        for (int i = 0; i < integerChromosome.length(); i++) {
            genes.add(integerChromosome.get(i));
        }

        return new SmodelIntegerChromosome(ISeq.of(genes), IntRange.of(1), optimizable, intRange.size() + 1);
    }

    @Override
    public IntegerChromosome newInstance(final ISeq<IntegerGene> genes) {
        return new SmodelIntegerChromosome(genes, lengthRange(), optimizable, numOfValues);
    }

    @Override
    public SmodelIntegerChromosome newInstance() {

        IntegerChromosome integerChromosome = IntegerChromosome.of(min(), max());
        List<IntegerGene> genes = new ArrayList();
        for (int i = 0; i < integerChromosome.length(); i++) {
            genes.add(integerChromosome.get(i));
        }

        return new SmodelIntegerChromosome(ISeq.of(genes), IntRange.of(1), optimizable, numOfValues);
    }

}
