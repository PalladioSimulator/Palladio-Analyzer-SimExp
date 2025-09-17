package org.palladiosimulator.simexp.dsl.ea.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import org.junit.Before;
import org.junit.Test;

public class RewardUtilTest {
    private final static double PRECISION = 0.00001;

    private final static double EPSILON = 0.0001;

    private RewardUtil rewardUtil;

    @Before
    public void setUp() throws Exception {
        this.rewardUtil = new RewardUtil(EPSILON);
    }

    @Test
    public void testGetPrecision() {
        double actualPrecision = rewardUtil.getPrecision();

        assertThat(actualPrecision).isEqualTo(EPSILON, withPrecision(PRECISION));
    }

    @Test
    public void testRoundWithin() {
        double reward = 0.9;

        double actualReward = rewardUtil.round(reward);

        assertThat(actualReward).isEqualTo(reward, withPrecision(PRECISION));
    }

    @Test
    public void testRoundBeyond() {
        double reward = 0.00002;

        double actualReward = rewardUtil.round(reward);

        assertThat(actualReward).isEqualTo(0.0, withPrecision(PRECISION));
    }
}
