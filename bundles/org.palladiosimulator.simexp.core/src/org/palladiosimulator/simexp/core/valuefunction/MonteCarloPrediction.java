package org.palladiosimulator.simexp.core.valuefunction;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MonteCarloPrediction implements ValueFunctionEstimator {

    private interface MonteCarloEstimator {

        public abstract void estimate(List<SimulatedExperience> traj);

    }

    private static class AccumulatedRewardManager {

        private final Map<String, List<Double>> managedAccRewards = Maps.newHashMap();

        public void append(String state, Double accReward) {
            List<Double> accRewards = managedAccRewards.get(state);
            if (accRewards == null) {
                managedAccRewards.put(state, Lists.newArrayList(accReward));
            } else {
                accRewards.add(accReward);
            }
        }

        public Double getAccumulatedRewardFor(String state) {
            List<Double> accRewards = managedAccRewards.get(state);
            if (accRewards == null) {
                return 0.0;
            }

            return accRewards.stream()
                .mapToDouble(a -> a)
                .average()
                .getAsDouble();
        }

    }

    private final ValueFunction valueFunction;
    private final AccumulatedRewardManager accRewardManager;

    private MonteCarloEstimator predictionEstimator;

    private MonteCarloPrediction() {
        this.valueFunction = new ValueFunction();
        this.accRewardManager = new AccumulatedRewardManager();
    }

    public static MonteCarloPrediction firstVisitEstimation() {
        MonteCarloPrediction estimation = new MonteCarloPrediction();
        estimation.predictionEstimator = estimation.firstVisitEstimator();
        return estimation;
    }

    @Override
    public ValueFunction estimate(Iterator<List<SimulatedExperience>> iterator) {
        while (iterator.hasNext()) {
            List<SimulatedExperience> traj = iterator.next();
            predictionEstimator.estimate(traj);
        }

        return valueFunction;
    }

    private MonteCarloEstimator firstVisitEstimator() {
        return new MonteCarloEstimator() {

            @Override
            public void estimate(List<SimulatedExperience> traj) {
                double accReward = 0;
                for (int t = traj.size() - 1; t >= 0; t--) {
                    SimulatedExperience sample = traj.get(t);

                    accReward += Double.parseDouble(sample.getReward());

                    String current = DefaultSimulatedExperience.getCurrentStateFrom(sample);
                    List<String> predecessors = traj.subList(0, t)
                        .stream()
                        .map(each -> DefaultSimulatedExperience.getCurrentStateFrom(each))
                        .collect(Collectors.toList());
                    if (isNotIncluded(current, predecessors)) {
                        accRewardManager.append(current, accReward);

                        double expectedReward = accRewardManager.getAccumulatedRewardFor(current);
                        valueFunction.updateExpectedReward(current, expectedReward);
                    }
                }
            }

            private boolean isNotIncluded(String current, List<String> predecessors) {
                return predecessors.stream()
                    .noneMatch(each -> each.equals(current));
            }
        };
    }

}
