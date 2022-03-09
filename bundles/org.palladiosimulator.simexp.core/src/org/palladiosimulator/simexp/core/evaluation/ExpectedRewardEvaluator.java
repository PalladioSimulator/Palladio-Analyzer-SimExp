package org.palladiosimulator.simexp.core.evaluation;

import java.util.Set;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.valuefunction.MonteCarloPrediction;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

import com.google.common.collect.Sets;

public class ExpectedRewardEvaluator implements TotalRewardCalculation {
	
	private final String simulationId;
	private final String sampleSpaceId;
	
	public ExpectedRewardEvaluator(String simulationId, String sampleSpaceId) {
		this.simulationId = simulationId;
		this.sampleSpaceId = sampleSpaceId;
	}
	
	@Override
	public double computeTotalReward() {
		SampleModelIterator iterator = SampleModelIterator.get(simulationId, sampleSpaceId);
		ValueFunction valueFunction = MonteCarloPrediction.firstVisitEstimation().estimate(iterator);
		
		double totalReward = 0;
		for (String each : filterInitialStates()) {
			double reward = valueFunction.getExpectedRewardFor(each);
			totalReward += reward;
		}

		return totalReward;
	}
	
	private Set<String> filterInitialStates() {
		Set<String> initials = Sets.newHashSet();
		
		SampleModelIterator iterator = SampleModelIterator.get(simulationId, sampleSpaceId);
		while (iterator.hasNext()) {
			String initial = DefaultSimulatedExperience.getCurrentStateFrom(iterator.next().get(0));
			if (initials.contains(initial) == false) {
				initials.add(initial);
			}
		}
		
		return initials;
	}

}
