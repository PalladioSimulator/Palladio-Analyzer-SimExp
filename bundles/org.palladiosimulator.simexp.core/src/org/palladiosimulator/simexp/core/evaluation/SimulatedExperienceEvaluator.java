package org.palladiosimulator.simexp.core.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;

public class SimulatedExperienceEvaluator implements TotalRewardCalculation {

    private final ISimulatedExperienceStore simulatedExperienceStore;

    private SimulatedExperienceEvaluator(ISimulatedExperienceStore simulatedExperienceStore) {
        this.simulatedExperienceStore = simulatedExperienceStore;
    }

    public static TotalRewardCalculation of(ISimulatedExperienceStore simulatedExperienceStore, String simulationId,
            String sampleSpaceId) {
        return new SimulatedExperienceEvaluator(simulatedExperienceStore);
    }

    @Override
    public double computeTotalReward() {
        double totalReward = 0;

        Iterator<List<SimulatedExperience>> iterator = simulatedExperienceStore.iterator();
        while (iterator.hasNext()) {
            List<SimulatedExperience> traj = iterator.next();
            totalReward += accumulateReward(traj.stream());
        }

        return totalReward;
    }

    private double accumulateReward(Stream<SimulatedExperience> traj) {
        return traj.map(each -> Double.parseDouble(each.getReward()))
            .reduce((r1, r2) -> r1 + r2)
            .get();
    }

    @Override
    public String getName() {
        return "accumulated";
    }

}
