package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;

public class SmodelGrayBitset extends SmodelBitset {

    private static final long serialVersionUID = 1L;

    public SmodelGrayBitset(int nbits) {
        super(nbits);
    }

    @Override
    public int toInt() {
        int idx = 0;
        if (length() > 0) {
            if (get(length() - 1)) {
                idx |= (1 << (length() - 1));
            }

            for (int i = length() - 2; i >= 0; i--) {
                boolean previousBinary = ((1 << (i + 1)) & idx) != 0;
                if (get(i) ^ previousBinary) {
                    idx |= (1 << i);
                }
            }
        }
        return idx;
    }

    @Override
    public void fromInt(int value) {
        if ((new PowerUtil()).minBitSizeForPower(value) > length()) {
            throw new RuntimeException("Given value needs more bits to encode than this bitset has");
        }
        int gray = value ^ (value >> 1);

        int i = 0;

        while (gray != 0) {
            if ((gray & 1) == 1) {
                set(i);
            }
            gray >>= 1;
            i++;
        }
    }

}
