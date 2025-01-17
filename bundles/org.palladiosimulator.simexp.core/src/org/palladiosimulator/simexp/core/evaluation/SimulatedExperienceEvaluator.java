package org.palladiosimulator.simexp.core.evaluation;

import java.util.List;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;

public class SimulatedExperienceEvaluator implements TotalRewardCalculation {

    private final SimulatedExperienceAccessor accessor;

    private SimulatedExperienceEvaluator(SimulatedExperienceAccessor accessor) {
        this.accessor = accessor;
    }

    public static TotalRewardCalculation of(SimulatedExperienceAccessor accessor, String simulationId,
            String sampleSpaceId) {
        return new SimulatedExperienceEvaluator(accessor);
    }

    @Override
    public double computeTotalReward() {
        double totalReward = 0;

        SampleModelIterator iterator = SampleModelIterator.get(accessor);
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
