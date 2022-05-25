package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

public class MonteCarloPredictionTest {
    

    private MonteCarloPrediction mcprediction;
    private ValueFunction valueFunction;

    @Before
    public void setUp() throws Exception {
        MonteCaroEstimator firstVisitMonteCarloEstimator = null;
        mcprediction = new MonteCarloPrediction(valueFunction, firstVisitMonteCarloEstimator);
    }

    @Ignore
    public void test() {
        fail("Not yet implemented");
    }

}
