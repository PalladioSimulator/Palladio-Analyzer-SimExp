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
        AccumulatedReward actual = new AccumulatedReward();
        
        actual.append(2.0);
        
        AccumulatedReward expected = new AccumulatedReward(2.0, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void testCalculateAverageRewardDefaultValue() throws Exception {
        AccumulatedReward accReward = new AccumulatedReward();
        
        double actual = accReward.calculateAverage();
        
        assertEquals(Double.NaN, actual, DELTA);
    }

    @Test
    public void testCalculateAverageReward() throws Exception {
        AccumulatedReward accReward = new AccumulatedReward(2.0, 1);
        
        double actual = accReward.calculateAverage();
        
        assertEquals(2.0, actual, DELTA);
    }
    
    @Test
    public void testCalculateAverageRewardFromSingleReward() {
        AccumulatedReward accReward = new AccumulatedReward(2.0, 2);
        double reward = 2.0;
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
        AccumulatedReward accReward = new AccumulatedReward(1.0, 1);
        AccumulatedReward mergeAccReward = new AccumulatedReward(3.0, 3);

        accReward.mergeWith(mergeAccReward);

        AccumulatedReward expected = new AccumulatedReward(4.0, 4);
        assertEquals(expected, accReward);
    }

}
