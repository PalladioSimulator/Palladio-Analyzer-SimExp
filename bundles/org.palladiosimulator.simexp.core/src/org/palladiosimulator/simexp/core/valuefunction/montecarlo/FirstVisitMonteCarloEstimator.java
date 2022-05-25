package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.palladiosimulator.simexp.core.entity.StateAwareSimulatedExperience;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;



/**
 * First-visit MC prediction for estimating v_pi (Sutton, p. 92)
 * 
 * 
 * https://www.reddit.com/r/reinforcementlearning/comments/9zkdjb/d_help_need_in_understanding_monte_carlo_first/
 * https://imgur.com/a/ZVxEnil
 * 
 * estimates value function for all states of a single trajectory
 * 
 * */
class FirstVisitMonteCarloEstimator extends MonteCaroEstimator {
    private final IAccumulatedRewardManager accRewardManager;
    private final ValueFunction valueFunction;

    public FirstVisitMonteCarloEstimator(IAccumulatedRewardManager accRewardManager, ValueFunction valueFunction) {
        this.accRewardManager = accRewardManager;
        this.valueFunction = valueFunction;
    }

    @Override
    public void estimate(Set<String> states, List<StateAwareSimulatedExperience> traj) {
        for (String state : states) {
            AccumulatedReward accReward = calculateAccRewardAfterFirstVisit(state, traj);
            accRewardManager.append(state, accReward);
            AccumulatedReward currentAccReward = accRewardManager.getAccumulatedReward(state);
            double currCalculatedAverage = currentAccReward.calculateAverage();
            valueFunction.updateExpectedReward(state, currCalculatedAverage);
        }
    }

    AccumulatedReward calculateAccRewardAfterFirstVisit(String state, List<StateAwareSimulatedExperience> traj) {
        List<StateAwareSimulatedExperience> subTrajectory = extractSubTrajectoryAfterFirstVisit(state, traj);
        AccumulatedReward accReward = new AccumulatedReward();
        for (StateAwareSimulatedExperience simulatedExperience : subTrajectory) {
            String reward = simulatedExperience.getReward();
            double rewardAsDouble = Double.parseDouble(reward);
            accReward.append(rewardAsDouble);
        }
        return accReward;
    }


    List<StateAwareSimulatedExperience> extractSubTrajectoryAfterFirstVisit(String state, List<StateAwareSimulatedExperience> traj) {
        int index = findIndexOfState(state, traj);
        if (index < 0) {
            // element not found in list
            return Collections.emptyList();
        }
        return new ArrayList<>(traj.subList(index, traj.size()));
    }

    private int findIndexOfState(String state, List<StateAwareSimulatedExperience> traj) {
//        Predicate<StateAwareSimulatedExperience> predicate = new Predicate<StateAwareSimulatedExperience>() {
//
//            @Override
//            public boolean test(StateAwareSimulatedExperience t) {
//                String currentState = t.getCurrentState();
//                if (state.equals(currentState)) {
//                    return true;
//                }
//                return false;
//            }
//        };

        return IntStream.range(0, traj.size())
            .filter(ix -> state.equals(traj.get(ix).getCurrentState()))
            //.filter(ix -> predicate.test(traj.get(ix)))
            .findFirst()
            .orElse(-1);
    }

}
