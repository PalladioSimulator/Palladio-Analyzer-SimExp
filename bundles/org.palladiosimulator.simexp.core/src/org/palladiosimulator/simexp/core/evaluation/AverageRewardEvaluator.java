package org.palladiosimulator.simexp.core.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;

public class AverageRewardEvaluator implements TotalRewardCalculation {
    private final ISimulatedExperienceAccessor accessor;

    public AverageRewardEvaluator(ISimulatedExperienceAccessor accessor) {
        this.accessor = accessor;
    }

    @Override
    public double computeTotalReward() {
        List<Double> sampleRewards = new ArrayList<>();
        SampleModelIterator iterator = SampleModelIterator.get(accessor);
        while (iterator.hasNext()) {
            List<SimulatedExperience> traj = iterator.next();
            sampleRewards.add(accumulateReward(traj.stream()));
        }

        double totalReward = sampleRewards.stream()
            .mapToDouble(Number::doubleValue)
            .average()
            .orElse(Double.NaN);
        return totalReward;
    }

    private double accumulateReward(Stream<SimulatedExperience> traj) {
        return traj.mapToDouble(each -> Double.parseDouble(each.getReward()))
            .sum();
    }

    @Override
    public String getName() {
        return "average";
    }
}
