package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.palladiosimulator.simexp.core.entity.StateAwareSimulatedExperience;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

public class ValueFunctionTest {

    private static final double DELTA = 1e-15;
    private ValueFunction valueFunction;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        valueFunction = new ValueFunction();
    }

    @Test
    public void testGetExpectedRewardForUnknownState() {
        Double expectedReward = Double.valueOf(0.0);
        Double actualReward = valueFunction.getExpectedRewardFor("unknownState");
        
        assertEquals(expectedReward, actualReward, DELTA);
    }
    
    @Test
    public void testGetExpectedRewardForState() throws Exception {
        String state = "state";
        Double expectedReward = Double.valueOf(4.0);
        valueFunction.updateExpectedReward(state, expectedReward);
        
        Double actualReward = valueFunction.getExpectedRewardFor(state);
        assertEquals(expectedReward, actualReward, DELTA);
    }
    
    @Test
    public void testGetExpectedRewardForUnknownSASState() throws Exception {
        Double expectedReward = Double.valueOf(0.0);
        SelfAdaptiveSystemState sasState = Mockito.mock(SelfAdaptiveSystemState.class);
        String state = "sasState";
        when(sasState.toString()).thenReturn(state);
        
        Double actualReward = valueFunction.getExpectedRewardFor(state);
        
        assertEquals(expectedReward, actualReward, DELTA);
    }


    @Test
    public void testGetExpectedRewardForSASState() throws Exception {
        Double expectedReward = Double.valueOf(4.0);
        SelfAdaptiveSystemState sasState = Mockito.mock(SelfAdaptiveSystemState.class);
        String state = "sasState";
        valueFunction.updateExpectedReward(state, expectedReward);
        when(sasState.toString()).thenReturn(state);
        
        Double actualReward = valueFunction.getExpectedRewardFor(state);
        
        assertEquals(expectedReward, actualReward, DELTA);
    }
}
