package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;

public class IndividualParetoResultTest {
    private final static double EPSILON = 0.00001;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBuildScoreEmpty() {
        IndividualResult individuaResult = new IndividualResult(0, Collections.emptyList());
        Map<String, Double> averages = Collections.emptyMap();
        IndividualParetoResult paretoResult = new IndividualParetoResult(individuaResult, averages);

        double actualScore = paretoResult.buildScore();

        assertThat(actualScore).isEqualTo(Double.NaN, withPrecision(EPSILON));
    }

    @Test
    public void testBuildScore() {
        IndividualResult individuaResult = new IndividualResult(0, Collections.emptyList());
        Map<String, Double> averages = new HashMap<>();
        averages.put("a", 0.3);
        averages.put("b", 0.7);
        IndividualParetoResult paretoResult = new IndividualParetoResult(individuaResult, averages);

        double actualScore = paretoResult.buildScore();

        assertThat(actualScore).isEqualTo(0.5, withPrecision(EPSILON));
    }

}
