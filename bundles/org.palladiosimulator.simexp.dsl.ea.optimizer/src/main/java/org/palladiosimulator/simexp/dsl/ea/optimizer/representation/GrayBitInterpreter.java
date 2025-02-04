package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.OptimizableChromosomeGrayConstraint;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;

import io.jenetics.BitGene;
import io.jenetics.engine.Constraint;
import io.jenetics.ext.moea.Vec;

public class GrayBitInterpreter implements BitInterpreter {

    private PowerUtil powerUtil = new PowerUtil();

    @Override
    public int toInt(SmodelBitset bitSet) {
        int idx = 0;
        if (bitSet.length() > 0) {
            if (bitSet.get(bitSet.length() - 1)) {
                idx |= (1 << (bitSet.length() - 1));
            }

            for (int i = bitSet.length() - 2; i >= 0; i--) {
                boolean previousBinary = ((1 << (i + 1)) & idx) != 0;
                if (bitSet.get(i) ^ previousBinary) {
                    idx |= (1 << i);
                }
            }
        }
        return idx;
    }

    @Override
    public BitSet toBitSet(int value) {
        BitSet bitSet = new BitSet();
        int gray = value ^ (value >> 1);
        int i = 0;
        while (gray != 0) {
            if ((gray & 1) == 1) {
                bitSet.set(i);
            } else {
                bitSet.clear(i);
            }
            gray >>= 1;
            i++;
        }
        return bitSet;
    }

    @Override
    public boolean isValid(SmodelBitset bitSet, int numOfValues) {
        return toInt(bitSet) < numOfValues;
        // TODO nbruening Add tests
    }

    // TODO nbruening Add tests
    @Override
    public int getMinimumLength(int power) {
        return powerUtil.minBitSizeForPower(power);
    }

    @Override
    public Constraint<BitGene, Vec<double[]>> getCorrespondingConstraint() {
        return new OptimizableChromosomeGrayConstraint();
    }

}
