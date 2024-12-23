package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

public class SmodelBitset extends FixedSizeBitSet {
    private static final long serialVersionUID = 1L;

    public SmodelBitset(int nbits) {
        super(nbits);
    }

    public int toInt() {
        int value = 0;
        for (int i = 0; i < length(); ++i) {
            value += get(i) ? (1L << i) : 0L;
        }
        return value;
    }
}
