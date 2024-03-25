package org.palladiosimulator.simexp.dsl.kmodel.interpreter.lookup.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.lookup.KModelLookup;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;

public class KModelLookupTest {

    private static KmodelFactory factory = KmodelFactory.eINSTANCE;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testFindModelName() {
        Kmodel kmodel = factory.createKmodel();
        String expectedName = "testStrategy";
        kmodel.setModelName(expectedName);
        KModelLookup lookup = new KModelLookup(kmodel);

        String actualName = lookup.findModelName();

        assertEquals(expectedName, actualName);
    }

}
