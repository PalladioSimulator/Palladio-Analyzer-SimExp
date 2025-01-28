package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;

public class BinaryBitInterpreter implements BitInterpreter {

    private PowerUtil powerUtil = new PowerUtil();

    @Override
    public int toInt(SmodelBitset bitSet) {
        int value = 0;
        for (int i = 0; i < bitSet.length(); ++i) {
            value += bitSet.get(i) ? (1L << i) : 0L;
        }
        return value;

    }

    @Override
    public BitSet toBitSet(int value) {
        return BitSet.valueOf(new long[] { value });
    }

    @Override
    public boolean isValid(SmodelBitset bitSet, int numOfValues) {
        return toInt(bitSet) < numOfValues;
        // TODO Add tests
    }

    @Override
    public int getMinimumLength(int power) {
        return powerUtil.minBitSizeForPower(power);
    }

}
