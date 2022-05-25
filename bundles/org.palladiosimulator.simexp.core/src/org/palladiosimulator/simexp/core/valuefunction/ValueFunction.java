package org.palladiosimulator.simexp.core.valuefunction;

import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

import com.google.common.collect.Maps;

public class ValueFunction implements IValueFunction {

	private final Map<String, Double> expectedRewards;
	
	public ValueFunction() {
		this.expectedRewards= Maps.newHashMap(); 
	}
	
	@Override
    public Double getExpectedRewardFor(String state) {
		return Optional.ofNullable(expectedRewards.get(state)).orElse(0.0);
	}
	
	@Override
    public Double getExpectedRewardFor(SelfAdaptiveSystemState<?> state) {
		return Optional.ofNullable(expectedRewards.get(state.toString())).orElse(0.0);
	}
	
	public void updateExpectedReward(String state, Double expReward) {
		expectedRewards.put(state, expReward);
	}

}
