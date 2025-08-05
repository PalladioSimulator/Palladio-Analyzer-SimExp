package org.palladiosimulator.core.simulation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

public interface SimulationExecutor {
    static class SimulationResult implements ISimulationResult {
        private final double totalReward;
        private final List<Map<String, List<Double>>> qos;
        private final String rewardDescription;

        public SimulationResult(double totalReward, List<Map<String, List<Double>>> qos, String rewardDescription) {
            this.totalReward = totalReward;
            this.qos = qos;
            this.rewardDescription = rewardDescription;
        }

        @Override
        public double getTotalReward() {
            return totalReward;
        }

        @Override
        public List<Map<String, List<Double>>> getQualityAttributes() {
            return qos;
        }

        @Override
        public String getRewardDescription() {
            return rewardDescription;
        }

        @Override
        public List<String> getDetailDescription() {
            return Collections.emptyList();
        }
    }

    String getPolicyId();

    void execute();

    ISimulationResult evaluate();

    void dispose();
}
