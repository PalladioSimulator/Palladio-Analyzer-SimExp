package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FixedSizeBitSetTest {

    @Test
    public void testGetNBits() {
        int nBits = 5;
        FixedSizeBitSet fixedSizeBitSet = new FixedSizeBitSet(nBits);

        int result = fixedSizeBitSet.getNbits();

        assertEquals(nBits, result);
    }

    @Test
    public void testGetNBitsZero() {
        int nBits = 0;
        FixedSizeBitSet fixedSizeBitSet = new FixedSizeBitSet(nBits);

        int result = fixedSizeBitSet.getNbits();

        assertEquals(nBits, result);
    }

}
