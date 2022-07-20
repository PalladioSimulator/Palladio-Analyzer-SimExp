package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KModelInterpreter;

public class KModelInterpreterTest {
    
    
    KModelInterpreter interpreter;
    
    @Before
    public void setUp() {
        interpreter = new KModelInterpreter();
    }

    @Test
    public void testHelloWorld() {
        String actual = interpreter.helloWorld();
        
        assertEquals("Hello World", actual);
    }

}
