package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AccumulatedRewardManagerTest {
    
    private IAccumulatedRewardManager accRewardManager;

    @Before
    public void setUp() throws Exception {
        accRewardManager = new AccumulatedRewardManager();
    }

    
    @Test
    public void testGetAccumulatedRewardForUnknownState() throws Exception {
        AccumulatedReward expected = new AccumulatedReward();
        
        AccumulatedReward actual = accRewardManager.getAccumulatedReward("unknownState");
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testGetAccRewardForKnownStated() {
        String state = "state";
        AccumulatedReward expectedReward = new AccumulatedReward();
        expectedReward.append(2.0);
        accRewardManager.append(state, expectedReward);
        
        AccumulatedReward actualReward = accRewardManager.getAccumulatedReward(state);
        
        assertEquals(expectedReward, actualReward);
    }

}
