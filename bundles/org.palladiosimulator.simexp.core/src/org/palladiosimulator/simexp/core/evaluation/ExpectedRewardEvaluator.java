package org.palladiosimulator.simexp.core.evaluation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;
import org.palladiosimulator.simexp.core.valuefunction.MonteCarloPrediction;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

public class ExpectedRewardEvaluator implements TotalRewardCalculation {

    private class InitialStateEstimator {

        private final List<String> sampledInitials;

        public InitialStateEstimator() {
            this.sampledInitials = filterSampledInitials();
        }

        private List<String> filterSampledInitials() {
            List<String> sampledInitials = new ArrayList<>();
            SampleModelIterator iterator = SampleModelIterator.get(simulatedExperienceStore);
            while (iterator.hasNext()) {
                List<SimulatedExperience> next = iterator.next();
                SimulatedExperience simExperience = next.get(0);
                String initial = DefaultSimulatedExperience.getCurrentStateFrom(simExperience);
                sampledInitials.add(initial);
            }

            return sampledInitials;
        }

        public Set<String> filterInitialStates() {
            Set<String> initials = new LinkedHashSet<>();
            for (String each : sampledInitials) {
                if (initials.contains(each) == false) {
                    initials.add(each);
                }
            }

            return initials;
        }

        public double estimateProbability(String initial) {
            double relativeCount = sampledInitials.stream()
                .filter(each -> each.equals(initial))
                .count();
            return relativeCount / sampledInitials.size();
        }

    }

    private final ISimulatedExperienceStore simulatedExperienceStore;

    public ExpectedRewardEvaluator(ISimulatedExperienceStore simulatedExperienceStore) {
        this.simulatedExperienceStore = simulatedExperienceStore;
    }

    @Override
    public double computeTotalReward() {
        SampleModelIterator iterator = SampleModelIterator.get(simulatedExperienceStore);
        MonteCarloPrediction firstVisitEstimation = MonteCarloPrediction.firstVisitEstimation();
        ValueFunction valueFunction = firstVisitEstimation.estimate(iterator);

        InitialStateEstimator initialStateEstimator = new InitialStateEstimator();

        double totalReward = 0;
        for (String each : initialStateEstimator.filterInitialStates()) {
            double reward = valueFunction.getExpectedRewardFor(each);
            double probability = initialStateEstimator.estimateProbability(each);
            totalReward += probability * reward;
        }

        return totalReward;
    }

    @Override
    public String getName() {
        return "expected";
    }
}
