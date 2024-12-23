package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SmodelBitChromosomeTest {
    private SmodelCreator smodelCreator;

    private Optimizable optimizable;

    @Before
    public void setUp() {
        smodelCreator = new SmodelCreator();
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
    }

    @Test
    public void testIntValue0() {
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable);

        int actualIntValue = chromosome.intValue();

        assertEquals(0, actualIntValue);
    }

    @Test
    public void testIntValue1() {
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable);

        int actualIntValue = chromosome.intValue();

        assertEquals(1, actualIntValue);
    }
}
