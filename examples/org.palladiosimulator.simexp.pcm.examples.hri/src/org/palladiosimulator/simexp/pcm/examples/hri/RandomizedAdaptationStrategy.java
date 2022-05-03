package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

import com.google.common.collect.Lists;

public class RandomizedAdaptationStrategy extends ReliabilityPrioritizedStrategy {

	public RandomizedAdaptationStrategy(SimulatedMeasurementSpecification responseTimeSpec) {
		super(responseTimeSpec);
	}

	@Override
	public String getId() {
		return "RandomizedAdaptationStrategy";
	}
	
	@Override
	protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
		List<QVToReconfiguration> availableOptions = Lists.newArrayList();
		
		if (isFilteringActivated) {
			availableOptions.add(deactivateFilteringReconfiguration(options));
		} else {
			availableOptions.add(activateFilteringReconfiguration(options));
		}
		
		if (isDefaultMLModelActivated) {
			availableOptions.add(switchToRobustMLModel(options));
		} else {
			availableOptions.add(switchToDefaultMLModel(options));
		}
		
		var randomlySelect = new Random().nextInt(2);
		return availableOptions.get(randomlySelect);
	}
	
}
