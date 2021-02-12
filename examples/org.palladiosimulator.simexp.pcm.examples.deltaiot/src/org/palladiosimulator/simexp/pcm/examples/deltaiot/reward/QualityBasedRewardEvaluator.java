package org.palladiosimulator.simexp.pcm.examples.deltaiot.reward;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class QualityBasedRewardEvaluator implements RewardEvaluator {

	public static class RealValuedReward extends RewardImpl<Double> {
		
		private RealValuedReward(double value) {
			super.setValue(value);
		}
		
		public static RealValuedReward of(double value) {
			return new RealValuedReward(value);
		}
		
		@Override
		public String toString() {
			return Double.toString(getValue());
		}
		
	}
	
	private final List<SimulatedMeasurementSpecification> specs;
	
	private QualityBasedRewardEvaluator(List<SimulatedMeasurementSpecification> specs) {
		this.specs = specs;
	}
	
	public static QualityBasedRewardEvaluator evaluateBy(List<? extends SimulatedMeasurementSpecification> specs) {
		requireNonNull(specs, "The measurement specs must not be null");
		if (specs.isEmpty()) {
			throw new IllegalArgumentException("The measurement specs must not be empty.");
		}
		
		var convertedSpecs = specs.stream()
				.map(SimulatedMeasurementSpecification.class::cast)
				.collect(toList());
		return new QualityBasedRewardEvaluator(convertedSpecs);
	}
	
	@Override
	public Reward<?> evaluate(StateQuantity quantifiedState) {
		double value = 0;
		for (SimulatedMeasurementSpecification each : specs) {
			var measurement = quantifiedState.findMeasurementWith(each).orElseThrow();
			value += measurement.getValue();
		}
		return RealValuedReward.of(value);
	}

}
