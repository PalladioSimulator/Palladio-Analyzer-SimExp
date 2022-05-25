package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.palladiosimulator.simexp.core.entity.StateAwareSimulatedExperience;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

public class FirstVisitMonteCarloEstimatorTest {
    
    @Mock private IAccumulatedRewardManager accRewardManager;
    private FirstVisitMonteCarloEstimator estimator;
    private ValueFunction valueFunction;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        valueFunction = new ValueFunction();
        estimator = new FirstVisitMonteCarloEstimator(accRewardManager, valueFunction);
    }
    
    
    @Test
    public void testFindFirstVisitSubtrajectoryWithinTrajectoryForState() throws Exception {
        String firstVisitState = "A";
        // sampled trajectory (state reward)-> B +3; A +2; B -3; terminate
        StateAwareSimulatedExperience sample1 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample2 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample3 = Mockito.mock(StateAwareSimulatedExperience.class);
        when(sample1.getCurrentState()).thenReturn("B");
        when(sample2.getCurrentState()).thenReturn("A");
        when(sample3.getCurrentState()).thenReturn("B");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReward()).thenReturn("2.0");
        when(sample3.getReward()).thenReturn("3.0");
        List<StateAwareSimulatedExperience> traj = Arrays.asList(sample1, sample2, sample3);
        
        List<StateAwareSimulatedExperience> actualSubtraj = estimator.extractSubTrajectoryAfterFirstVisit(firstVisitState, traj);
    
        // expected subtrajectory:  A+2; B-4; terminate -> size 2
        assertEquals(2, actualSubtraj.size());
    }
    
    @Test
    public void testFindFirstVisitSubtrajectorWithinTrajectoryForUnknownState() throws Exception {
        String firstVisitState = "C";
        // sampled trajectory (state reward)-> B +3; A +2; terminate
        StateAwareSimulatedExperience sample1 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample2 = Mockito.mock(StateAwareSimulatedExperience.class);
        when(sample1.getCurrentState()).thenReturn("B");
        when(sample2.getCurrentState()).thenReturn("A");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReward()).thenReturn("2.0");
        List<StateAwareSimulatedExperience> traj = Arrays.asList(sample1, sample2);
        
        List<StateAwareSimulatedExperience> actualSubtraj = estimator.extractSubTrajectoryAfterFirstVisit(firstVisitState, traj);
        
        assertEquals(0, actualSubtraj.size());  // expected subtrajectory: empty -> size 0
    }
    
    
    @Test
    public void testCalculateAccumulatedRewardAfterFirstVisit() throws Exception {
        String firstVisitState = "A";
        // sampled trajectory (state reward)-> B +3; A +2; B -3; terminate
        StateAwareSimulatedExperience sample1 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample2 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample3 = Mockito.mock(StateAwareSimulatedExperience.class);
        when(sample1.getCurrentState()).thenReturn("B");
        when(sample2.getCurrentState()).thenReturn("A");
        when(sample3.getCurrentState()).thenReturn("B");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReward()).thenReturn("2.0");
        when(sample3.getReward()).thenReturn("3.0");
        List<StateAwareSimulatedExperience> traj = Arrays.asList(sample1, sample2, sample3);
        
        AccumulatedReward actual = estimator.calculateAccRewardAfterFirstVisit(firstVisitState, traj);
        
        AccumulatedReward expected = new AccumulatedReward(5.0, 3);
        assertEquals(expected, actual);
    }
    

    @Test
    public void testCalculateAccumulatedRewardAfterFirstVisitForUnknownState() throws Exception {
        String firstVisitState = "C";
        // sampled trajectory (state reward)-> B +3; A +2; B -3; terminate
        StateAwareSimulatedExperience sample1 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample2 = Mockito.mock(StateAwareSimulatedExperience.class);
        when(sample1.getCurrentState()).thenReturn("B");
        when(sample2.getCurrentState()).thenReturn("A");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReward()).thenReturn("2.0");
        List<StateAwareSimulatedExperience> traj = Arrays.asList(sample1, sample2);
        
        AccumulatedReward actual = estimator.calculateAccRewardAfterFirstVisit(firstVisitState, traj);
        
        AccumulatedReward expected = new AccumulatedReward(0.0, 1);
        assertEquals(expected, actual);
    }

    
    @Test
    public void testEstimateValueFunctionForOneState() {
        String firstVisitState = "A";
        // sampled trajectory (state reward)-> B +3; A +2; B -3; terminate
        StateAwareSimulatedExperience sample1 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample2 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample3 = Mockito.mock(StateAwareSimulatedExperience.class);
        when(sample1.getCurrentState()).thenReturn("B");
        when(sample2.getCurrentState()).thenReturn("A");
        when(sample3.getCurrentState()).thenReturn("B");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReward()).thenReturn("2.0");
        when(sample3.getReward()).thenReturn("3.0");
        List<StateAwareSimulatedExperience> traj = Arrays.asList(sample1, sample2, sample3);
        Set<String> states = new HashSet<>(Arrays.asList(firstVisitState));
        AccumulatedReward accReward = new AccumulatedReward();
        accReward.append(5.0);
        when(accRewardManager.getAccumulatedReward(firstVisitState)).thenReturn(accReward);
        
        estimator.estimate(states, traj);
        
        Double actualReward = valueFunction.getExpectedRewardFor(firstVisitState);
        Double expectedReward = 2.5;
        assertEquals(expectedReward, actualReward);
    }
    
    
    @Test
    public void testEstimateValueForOneExperimentWithStates() throws Exception {
        String firstVisitState = "A";
        String firstVisitStateB = "B";
        // sampled trajectory (state reward)-> B +3; A +2; B -3; terminate
        StateAwareSimulatedExperience sample1 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample2 = Mockito.mock(StateAwareSimulatedExperience.class);
        StateAwareSimulatedExperience sample3 = Mockito.mock(StateAwareSimulatedExperience.class);
        when(sample1.getCurrentState()).thenReturn("B");
        when(sample2.getCurrentState()).thenReturn("A");
        when(sample3.getCurrentState()).thenReturn("B");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReward()).thenReturn("2.0");
        when(sample3.getReward()).thenReturn("3.0");
        List<StateAwareSimulatedExperience> traj = Arrays.asList(sample1, sample2, sample3);
        Set<String> states = new HashSet<>(Arrays.asList(firstVisitState, firstVisitStateB));
        AccumulatedReward accRewardA = new AccumulatedReward();
        accRewardA.append(5.0);
        AccumulatedReward accRewardB = new AccumulatedReward();
        accRewardA.append(8.0);
        when(accRewardManager.getAccumulatedReward(firstVisitState)).thenReturn(accRewardA);
        when(accRewardManager.getAccumulatedReward(firstVisitStateB)).thenReturn(accRewardB);


        estimator.estimate(states, traj);
        
        Double expected = 4.333333333333333;
        Double actual1 = valueFunction.getExpectedRewardFor(firstVisitState);
        assertEquals(expected, actual1);
    }
    
}
