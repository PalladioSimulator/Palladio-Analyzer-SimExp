package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import java.util.BitSet;

public class FixedSizeBitSet extends BitSet {

    private static final long serialVersionUID = 1L;

    private final int nbits;

    public FixedSizeBitSet(int nbits) {
        super(nbits);
        this.nbits = nbits;
    }

    public int getNbits() {
        return nbits;
    }
}
