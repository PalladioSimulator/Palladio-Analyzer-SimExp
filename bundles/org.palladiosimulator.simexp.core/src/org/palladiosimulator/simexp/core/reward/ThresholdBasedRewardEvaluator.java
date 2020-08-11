package org.palladiosimulator.simexp.core.reward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;

public class ThresholdBasedRewardEvaluator implements RewardEvaluator {
	
	private final List<Pair<SimulatedMeasurementSpecification, Threshold>> thresholds;
	
	protected ThresholdBasedRewardEvaluator(List<Pair<SimulatedMeasurementSpecification, Threshold>> thresholds) {
		this.thresholds = thresholds;
	}
	
	@SafeVarargs
	public static ThresholdBasedRewardEvaluator with(Pair<SimulatedMeasurementSpecification, Threshold>...thresholds) {
		return new ThresholdBasedRewardEvaluator(Arrays.asList(thresholds));
	}

	@Override
	public Reward<Integer> evaluate(StateQuantity quantity) {
		List<Pair<SimulatedMeasurement, Threshold>> thresholds = filterThresholds(quantity);
		if (thresholds.isEmpty()) {
			//TODO exception handling
			throw new RuntimeException("");
		}
		
		for (Pair<SimulatedMeasurement, Threshold> each : thresholds) {
			SimulatedMeasurement measurement = each.getFirst();
			Threshold threshold = each.getSecond();
			if (threshold.isNotSatisfied(measurement.getValue())) {
				return SimpleRewardSignal.createNegativReward();
			}
		}
		return SimpleRewardSignal.createPositivReward();
	}
	
	protected List<Pair<SimulatedMeasurement, Threshold>> filterThresholds(StateQuantity quantity) {
		List<Pair<SimulatedMeasurement, Threshold>> matches = new ArrayList<>();
		for (Pair<SimulatedMeasurementSpecification, Threshold> each : thresholds) {
			Optional<SimulatedMeasurement> match = quantity.findMeasurementWith(each.getFirst());
			if (match.isPresent()) {
				matches.add(Pair.of(match.get(), each.getSecond()));
			}
		}
		return matches;
	}
}
