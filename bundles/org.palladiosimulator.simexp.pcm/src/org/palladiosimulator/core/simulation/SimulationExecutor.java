package org.palladiosimulator.core.simulation;

public interface SimulationExecutor {
    static class SimulationResult {
        private final double totalReward;
        private final String rewardDescription;

        public SimulationResult(double totalReward, String rewardDescription) {
            this.totalReward = totalReward;
            this.rewardDescription = rewardDescription;
        }

        public double getTotalReward() {
            return totalReward;
        }

        public String getRewardDescription() {
            return rewardDescription;
        }
    }

    String getPolicyId();

    void execute();

    SimulationResult evaluate();

    void dispose();
}
