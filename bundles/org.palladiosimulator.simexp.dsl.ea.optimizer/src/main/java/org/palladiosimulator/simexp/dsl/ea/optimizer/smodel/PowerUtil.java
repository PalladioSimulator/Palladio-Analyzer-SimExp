package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

public class PowerUtil {
    public int minBitSizeForPower(int power) {
        int minSize = (int) Math.ceil(Math.log(power) / Math.log(2));
        return minSize;
    }
}
