package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static java.util.Objects.requireNonNull;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.internal.util.Bits;
import io.jenetics.util.ISeq;

public class SmodelBitChromosome extends BitChromosome {
    private static final long serialVersionUID = 1L;

    private final Optimizable optimizable;

    private SmodelBitChromosome(Optimizable optimizable, byte[] bits, int length) {
        super(bits, 0, length);
        this.optimizable = optimizable;
    }

    public Optimizable getOptimizable() {
        return optimizable;
    }

    @Override
    public boolean isValid() {
        // TODO: check for gene length's
        return true;
    }

    @Override
    public int intValue() {
        SmodelBitset bitSet = toBitSet();
        return bitSet.toInt();
    }

    @Override
    public SmodelBitset toBitSet() {
        final SmodelBitset set = new SmodelBitset(length());
        for (int i = 0, n = length(); i < n; ++i) {
            set.set(i, get(i).bit());
        }
        return set;
    }

    @Override
    public SmodelBitChromosome newInstance(final ISeq<BitGene> genes) {
        requireNonNull(genes, "Genes");
        if (genes.isEmpty()) {
            throw new IllegalArgumentException("The genes sequence must contain at least one gene.");
        }

        final SmodelBitChromosome chromosome = new SmodelBitChromosome(getOptimizable(), Bits.newArray(genes.length()),
                genes.length());
        int ones = 0;

        for (int i = genes.length(); --i >= 0;) {
            if (genes.get(i)
                .booleanValue()) {
                Bits.set(chromosome._genes, i);
                ++ones;
            }
        }

        chromosome._p = (double) ones / (double) genes.length();
        return chromosome;
    }

    @Override
    public SmodelBitChromosome newInstance() {
        // return of(_length, _p);
        throw new RuntimeException("not supported");
    }

    public static SmodelBitChromosome of(final SmodelBitset bits, Optimizable optimizable) {
        int length = bits.getNbits();
        final byte[] bytes = Bits.newArray(length);
        for (int i = 0; i < length; ++i) {
            if (bits.get(i)) {
                Bits.set(bytes, i);
            }
        }
        return new SmodelBitChromosome(optimizable, bytes, length);
    }
}
