package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

public class PowerUtil {
    public int minBitSizeForPower(int power) {
        if (power == 0) {
            return 1;
        }
        int minSize = (int) Math.ceil(Math.log(power) / Math.log(2));
        return minSize;
    }
}
