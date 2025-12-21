package org.palladiosimulator.simexp.core.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;

public class AverageRewardEvaluatorTest {
    private static final double EPSILON = 0.0001;

    private AverageRewardEvaluator evaluator;

    @Mock
    private ISimulatedExperienceAccessor accessor;
    @Mock
    private SimulatedExperienceReadAccessor readAccessor;
    @Mock
    private SimulatedExperience simulatedExperience1;
    @Mock
    private SimulatedExperience simulatedExperience2;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(accessor.createSimulatedExperienceReadAccessor()).thenReturn(readAccessor);

        evaluator = new AverageRewardEvaluator(accessor);
    }

    @Test
    public void testComputeTotalReward() {
        when(readAccessor.existTrajectoryAt(0)).thenReturn(true);
        when(simulatedExperience1.getReward()).thenReturn("1.0");
        when(simulatedExperience2.getReward()).thenReturn("2.0");
        when(readAccessor.getTrajectoryAt(0)).thenReturn(Optional.of(Arrays.asList(simulatedExperience1, simulatedExperience2)));
        double actualTotalReward = evaluator.computeTotalReward();

        assertThat(actualTotalReward).isEqualTo(3.0, withPrecision(EPSILON));
    }

}
