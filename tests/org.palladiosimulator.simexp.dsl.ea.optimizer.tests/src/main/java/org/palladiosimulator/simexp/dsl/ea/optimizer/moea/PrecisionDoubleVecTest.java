package org.palladiosimulator.simexp.dsl.ea.optimizer.moea;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class PrecisionDoubleVecTest {
    private static final double EPSILON = 0.01;

    private PrecisionDoubleVec vec;

    @Before
    public void setUp() throws Exception {
        vec = new PrecisionDoubleVec(EPSILON, new double[] { 0.0 });
    }

    @Test
    public void testCompareDoubleEqualExact() {
        int actualResult = vec.compareDouble(0.1, 0.1);

        assertEquals(0, actualResult);
    }

    @Test
    public void testCompareDoubleEqualClose() {
        int actualResult = vec.compareDouble(0.011, 0.012);

        assertEquals(0, actualResult);
    }

    @Test
    public void testCompareDoubleLargerExact() {
        int actualResult = vec.compareDouble(0.2, 0.1);

        assertEquals(1, actualResult);
    }

    @Test
    public void testCompareDoubleLargerClose() {
        int actualResult = vec.compareDouble(0.021, 0.02);

        assertEquals(0, actualResult);
    }

    @Test
    public void testCompareDoubleSmallerExact() {
        int actualResult = vec.compareDouble(0.1, 0.2);

        assertEquals(-1, actualResult);
    }

    @Test
    public void testCompareDoubleSmallerClose() {
        int actualResult = vec.compareDouble(0.02, 0.021);

        assertEquals(0, actualResult);
    }
}
