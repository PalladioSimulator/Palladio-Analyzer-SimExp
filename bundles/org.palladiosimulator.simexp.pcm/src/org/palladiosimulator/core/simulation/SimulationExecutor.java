package org.palladiosimulator.core.simulation;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

public interface SimulationExecutor {
    static class SimulationResult implements ISimulationResult {
        private final double totalReward;
        private final QualityMeasurements qualityMeasurements;
        private final String rewardDescription;

        public SimulationResult(double totalReward, QualityMeasurements qualityMeasurements, String rewardDescription) {
            this.totalReward = totalReward;
            this.qualityMeasurements = qualityMeasurements;
            this.rewardDescription = rewardDescription;
        }

        @Override
        public double getTotalReward() {
            return totalReward;
        }

        @Override
        public QualityMeasurements getQualityMeasurements() {
            return qualityMeasurements;
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
