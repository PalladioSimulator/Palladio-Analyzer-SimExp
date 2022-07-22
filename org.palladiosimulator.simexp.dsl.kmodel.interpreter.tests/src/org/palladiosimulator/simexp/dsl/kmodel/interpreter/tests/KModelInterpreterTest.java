package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KmodelInterpreter;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;

public class KModelInterpreterTest {
    private KmodelInterpreter interpreter;
    private ProbeValueProvider mockedPvp;
    private VariableValueProvider mockedVvp;
    
    
    @Before
    public void setUp() {
        mockedPvp = mock(ProbeValueProvider.class);
        mockedVvp = mock(VariableValueProvider.class);
        
        
    }
    
}
