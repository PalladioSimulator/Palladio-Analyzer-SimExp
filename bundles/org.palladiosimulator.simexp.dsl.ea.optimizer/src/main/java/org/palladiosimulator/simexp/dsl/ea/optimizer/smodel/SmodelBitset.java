package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public class SmodelBitset extends FixedSizeBitSet {
    private static final long serialVersionUID = 1L;

    private final Optimizable optimizable;

    public SmodelBitset(Optimizable optimizable, int nbits) {
        super(nbits);
        this.optimizable = optimizable;
    }
}
