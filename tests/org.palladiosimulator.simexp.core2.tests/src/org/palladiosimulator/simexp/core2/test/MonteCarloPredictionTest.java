package org.palladiosimulator.simexp.core2.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.evaluation.SampleModelIterator;
import org.palladiosimulator.simexp.core.valuefunction.MonteCarloPrediction;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

public class MonteCarloPredictionTest {


    private static final double DELTA = 1e-15;

    private MonteCarloPrediction mcPrediction;
    private ValueFunction valueFunction;
//    private MonteCaroEstimator predictionEstimator;
//    private IAccumulatedRewardManager accRewardManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        accRewardManager = new AccumulatedRewardManager();
        valueFunction = new ValueFunction();
//        predictionEstimator = new FirstVisitMonteCarloEstimator(accRewardManager, valueFunction);
//        mcPrediction = new MonteCarloPrediction(valueFunction, predictionEstimator);
        mcPrediction = MonteCarloPrediction.firstVisitEstimation();
    }

    @Test
    public void testEstimateValueFunctionForTrajectories() {
        SampleModelIterator sampleModel = testHelperCreateSampleTrajectories();
        
        ValueFunction actual = mcPrediction.estimate(sampleModel);
        
        assertEquals(1.0, actual.getExpectedRewardFor("A").doubleValue(), DELTA);
        assertEquals(-2.5, actual.getExpectedRewardFor("B").doubleValue(), DELTA);
    }
    
    private SampleModelIterator testHelperCreateSampleTrajectories() {
        SampleModelIterator sampleModel = Mockito.mock(SampleModelIterator.class);
        // Sampled trajectory1: ("A", 3), ("A", 2), ("B", -4), ("A", 4), ("B", -3)), terminate
        SimulatedExperience sample1 = Mockito.mock(SimulatedExperience.class);
        SimulatedExperience sample2 = Mockito.mock(SimulatedExperience.class);
        SimulatedExperience sample3 = Mockito.mock(SimulatedExperience.class);
        SimulatedExperience sample4 = Mockito.mock(SimulatedExperience.class);
        SimulatedExperience sample5 = Mockito.mock(SimulatedExperience.class);
        when(sample1.getReconfiguration()).thenReturn("A");
        when(sample1.getReward()).thenReturn("3.0");
        when(sample2.getReconfiguration()).thenReturn("A");
        when(sample2.getReward()).thenReturn("2.0");
        when(sample3.getReconfiguration()).thenReturn("B");
        when(sample3.getReward()).thenReturn("-4.0");
        when(sample4.getReconfiguration()).thenReturn("A");
        when(sample4.getReward()).thenReturn("4.0");
        when(sample5.getReconfiguration()).thenReturn("B");
        when(sample5.getReward()).thenReturn("-3.0");
        List<SimulatedExperience> traj1 = Arrays.asList(sample1, sample2, sample3, sample4, sample5);
        
        // // Sampled trajectory2: ("B", -2), ("A", 3), ("B", -3), terminate
//        StateAwareSimulatedExperience sample6 = Mockito.mock(StateAwareSimulatedExperience.class);
//        StateAwareSimulatedExperience sample7 = Mockito.mock(StateAwareSimulatedExperience.class);
//        StateAwareSimulatedExperience sample8 = Mockito.mock(StateAwareSimulatedExperience.class);
        SimulatedExperience sample6 = Mockito.mock(SimulatedExperience.class);
        SimulatedExperience sample7 = Mockito.mock(SimulatedExperience.class);
        SimulatedExperience sample8 = Mockito.mock(SimulatedExperience.class);
        when(sample6.getReconfiguration()).thenReturn("A");
        when(sample6.getReward()).thenReturn("3.0");
        when(sample7.getReconfiguration()).thenReturn("A");
        when(sample7.getReward()).thenReturn("2.0");
        when(sample8.getReconfiguration()).thenReturn("B");
        when(sample8.getReward()).thenReturn("-4.0");
        List<SimulatedExperience> traj2 = Arrays.asList(sample6, sample7, sample8);
        
        // two trajectory
        when(sampleModel.hasNext()).thenReturn(true, true, false);
        when(sampleModel.next()).thenReturn(traj1, traj2);
        
        return sampleModel;
    }


}
