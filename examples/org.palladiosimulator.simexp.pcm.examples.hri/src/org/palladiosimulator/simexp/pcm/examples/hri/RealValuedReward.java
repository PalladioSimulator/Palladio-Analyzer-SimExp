package org.palladiosimulator.simexp.pcm.examples.hri;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class RealValuedReward extends RewardImpl<Double> {
	public RealValuedReward(double value) {
		super.setValue(value);
	}
	
	@Override
	public String toString() {
		return Double.toString(getValue());
	}
}
