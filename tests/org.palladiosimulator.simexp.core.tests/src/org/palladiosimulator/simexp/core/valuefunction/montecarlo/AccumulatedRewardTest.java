package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AccumulatedRewardTest {
    
    private static final double DELTA = 1e-15;

    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testAppendReward() throws Exception {
        AccumulatedReward expected = new AccumulatedReward(2.0, 2);
        AccumulatedReward actual = new AccumulatedReward();
        
        actual.append(2.0);
        
        assertEquals(expected, actual);
    }

    @Test
    public void testCalculateAverageRewardDefaultValue() throws Exception {
        AccumulatedReward accReward = new AccumulatedReward();
        
        double actual = accReward.calculateAverage();
        
        assertEquals(0.0, actual, DELTA);
    }

    @Test
    public void testCalculateAverageReward() throws Exception {
        AccumulatedReward accReward = new AccumulatedReward(4.0, 1);
        
        double actual = accReward.calculateAverage();
        
        assertEquals(2.0, actual, DELTA);
    }
    
    @Test
    public void testCalculateAverageRewardFromSingleReward() {
        AccumulatedReward accReward = new AccumulatedReward();
        double reward = 4.0;
        accReward.append(reward);
        
        double actual = accReward.calculateAverage();

        assertEquals(2.0, actual, DELTA);
    }

    @Test
    public void testCalculateAverageRewardFromTwoRewards() {
        AccumulatedReward accReward = new AccumulatedReward();
        accReward.append(2.0);
        accReward.append(4.0);
        
        double actual = accReward.calculateAverage();
        
        assertEquals(2.0, actual, DELTA);
    }
    
    
    @Test
    public void testMergeAccRewards(){
        AccumulatedReward accReward = new AccumulatedReward();
        AccumulatedReward mergeAccReward = new AccumulatedReward(4.0, 2);
        accReward.append(1.0);
        accReward.mergeWith(mergeAccReward);

        double actual = accReward.calculateAverage();
        
        assertEquals(1.25, actual, DELTA);
    }

}
