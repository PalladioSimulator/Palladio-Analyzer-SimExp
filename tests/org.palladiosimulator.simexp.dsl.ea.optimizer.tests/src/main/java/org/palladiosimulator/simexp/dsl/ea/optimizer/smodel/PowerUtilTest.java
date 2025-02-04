package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class PowerUtilTest {
    private PowerUtil powerUtil;

    @Before
    public void setUp() {
        powerUtil = new PowerUtil();
    }

    @Test
    public void testPower0() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(0);

        assertEquals(0, actualMinBitSize);
    }

    @Test
    public void testPower1() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(1);

        assertEquals(0, actualMinBitSize);
    }

    @Test
    public void testPower2() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(2);

        assertEquals(1, actualMinBitSize);
    }

    @Test
    public void testPower5() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(5);

        assertEquals(3, actualMinBitSize);
    }
}
