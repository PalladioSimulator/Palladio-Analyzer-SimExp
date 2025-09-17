package org.palladiosimulator.simexp.dsl.ea.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import org.junit.Before;
import org.junit.Test;

public class RewardFormaterTest {
    private final static double EPSILON = 0.00001;

    private final static int PLACES = 3;

    private RewardFormater rewardFormater;

    @Before
    public void setUp() throws Exception {
        this.rewardFormater = new RewardFormater(PLACES);
    }

    @Test
    public void testGetPrecision() {
        double actualPrecision = rewardFormater.getPrecision();

        assertThat(actualPrecision).isEqualTo(0.0001, withPrecision(EPSILON));
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
