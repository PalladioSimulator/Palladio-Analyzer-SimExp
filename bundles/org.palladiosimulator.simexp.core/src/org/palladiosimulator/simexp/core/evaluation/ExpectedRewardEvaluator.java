package org.palladiosimulator.simexp.core.evaluation;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.valuefunction.MonteCarloPrediction;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ExpectedRewardEvaluator implements TotalRewardCalculation {

    private class InitialStateEstimator {

        private final List<String> sampledInitials;

        public InitialStateEstimator() {
            this.sampledInitials = filterSampledInitials();
        }

        private List<String> filterSampledInitials() {
            List<String> sampledInitials = Lists.newArrayList();

            SampleModelIterator iterator = SampleModelIterator.get(simulationId, sampleSpaceId);
            while (iterator.hasNext()) {
                String initial = DefaultSimulatedExperience.getCurrentStateFrom(iterator.next()
                    .get(0));
                sampledInitials.add(initial);
            }

            return sampledInitials;
        }

        public Set<String> filterInitialStates() {
            Set<String> initials = Sets.newLinkedHashSet();

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

    private final String simulationId;
    private final String sampleSpaceId;

    public ExpectedRewardEvaluator(String simulationId, String sampleSpaceId) {
        this.simulationId = simulationId;
        this.sampleSpaceId = sampleSpaceId;
    }

    @Override
    public double computeTotalReward() {
        SampleModelIterator iterator = SampleModelIterator.get(simulationId, sampleSpaceId);
        ValueFunction valueFunction = MonteCarloPrediction.firstVisitEstimation()
            .estimate(iterator);

        InitialStateEstimator initialStateEstimator = new InitialStateEstimator();

        double totalReward = 0;
        for (String each : initialStateEstimator.filterInitialStates()) {
            double reward = valueFunction.getExpectedRewardFor(each);
            double probability = initialStateEstimator.estimateProbability(each);
            totalReward += probability * reward;
        }

        return totalReward;
    }

}
