package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;

public class PowerUtilTest {
    private PowerUtil powerUtil;

    @Mock
    private IExpressionCalculator calculator;

    @Before
    public void setUp() {
        initMocks(this);
        powerUtil = new PowerUtil(calculator);
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
