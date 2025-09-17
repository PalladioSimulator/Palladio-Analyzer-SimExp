package org.palladiosimulator.simexp.dsl.ea.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

public class RewardFormaterTest {
    private final static double EPSILON = 0.00001;
    private final static int PLACES = 3;
    private final static double PRECISION = 0.0001;

    private RewardFormater rewardFormater;

    @Mock
    private IPrecisionProvider precisionProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(precisionProvider.getPlaces()).thenReturn(PLACES);
        when(precisionProvider.getPrecision()).thenReturn(PRECISION);

        this.rewardFormater = new RewardFormater(precisionProvider);
    }

    @Test
    public void testRoundWithin() {
        double reward = 0.9;

        double actualReward = rewardFormater.round(reward);

        assertThat(actualReward).isEqualTo(reward, withPrecision(EPSILON));
    }

    @Test
    public void testRoundBeyond() {
        double reward = 0.00002;

        double actualReward = rewardFormater.round(reward);

        assertThat(actualReward).isEqualTo(0.0, withPrecision(EPSILON));
    }
}
