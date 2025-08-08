package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.jenetics.IntegerGene;

public class EvaluationStatisticsTest {
    private EvaluationStatistics<IntegerGene> statistics;

    @Mock
    private MOEAFitnessFunction<IntegerGene> fitnessFunction;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        statistics = new EvaluationStatistics<>(fitnessFunction, BigInteger.valueOf(1l));
    }

    @Test
    public void testToRoundedString1() {
        String actualRoundedString = statistics.toRoundedString(BigInteger.valueOf(1), BigInteger.valueOf(100));

        assertThat(actualRoundedString).isEqualTo("1");
    }

    @Test
    public void testToRoundedString100() {
        String actualRoundedString = statistics.toRoundedString(BigInteger.valueOf(100), BigInteger.valueOf(100));

        assertThat(actualRoundedString).isEqualTo("100");
    }

    @Test
    public void testToRoundedStringInfiniteResult() {
        String actualRoundedString = statistics.toRoundedString(BigInteger.valueOf(66), BigInteger.valueOf(84));

        assertThat(actualRoundedString).isEqualTo("78.57");
    }
}
