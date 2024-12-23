package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SmodelBitsetTest {
    private SmodelBitset bitSet;

    @Before
    public void setUp() {
        bitSet = new SmodelBitset(3);
    }

    @Test
    public void testToInt0() {
        int actualInt = bitSet.toInt();

        assertEquals(0, actualInt);
    }

    @Test
    public void testToInt1() {
        bitSet.set(0);

        int actualInt = bitSet.toInt();

        assertEquals(1, actualInt);
    }

    @Test
    public void testToInt2() {
        bitSet.set(1);

        int actualInt = bitSet.toInt();

        assertEquals(2, actualInt);
    }

    @Test
    public void testToInt3() {
        bitSet.set(0);
        bitSet.set(1);

        int actualInt = bitSet.toInt();

        assertEquals(3, actualInt);
    }
}
