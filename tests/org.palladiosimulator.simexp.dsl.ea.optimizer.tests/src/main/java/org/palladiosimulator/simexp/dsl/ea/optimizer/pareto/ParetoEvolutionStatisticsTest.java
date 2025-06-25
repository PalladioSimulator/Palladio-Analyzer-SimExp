package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.jenetics.IntegerGene;

public class ParetoEvolutionStatisticsTest {
    private ParetoEvolutionStatistics<IntegerGene> statistics;

    @Mock
    private MOEAFitnessFunction<IntegerGene> fitnessFunction;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        statistics = new ParetoEvolutionStatistics<>(fitnessFunction, 1l);
    }

    @Test
    public void testToRoundedString1() {
        String actualRoundedString = statistics.toRoundedString(1, 100);

        assertThat(actualRoundedString).isEqualTo("1");
    }

    @Test
    public void testToRoundedString100() {
        String actualRoundedString = statistics.toRoundedString(100, 100);

        assertThat(actualRoundedString).isEqualTo("100");
    }
}
