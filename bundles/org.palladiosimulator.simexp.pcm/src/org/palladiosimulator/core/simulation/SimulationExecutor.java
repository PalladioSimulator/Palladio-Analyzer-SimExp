package org.palladiosimulator.core.simulation;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

public interface SimulationExecutor {
    static class SimulationResult implements ISimulationResult {
        private final double totalReward;
        private final String rewardDescription;

        public SimulationResult(double totalReward, String rewardDescription) {
            this.totalReward = totalReward;
            this.rewardDescription = rewardDescription;
        }

        @Override
        public double getTotalReward() {
            return totalReward;
        }

        public String getRewardDescription() {
            return rewardDescription;
        }

        public List<String> getDetailDescription() {
            return Collections.emptyList();
        }
    }

    String getPolicyId();

    void execute();

    SimulationResult evaluate();

    void dispose();
}
