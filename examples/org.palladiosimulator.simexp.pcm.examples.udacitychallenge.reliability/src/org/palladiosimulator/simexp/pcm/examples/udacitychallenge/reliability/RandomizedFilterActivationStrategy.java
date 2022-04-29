package org.palladiosimulator.simexp.pcm.examples.udacitychallenge.reliability;

import java.util.Random;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public class RandomizedFilterActivationStrategy extends ImageBlurMitigationStrategy {

	@Override
	public String getId() {
		return RandomizedFilterActivationStrategy.class.getName();
	}

	@Override
	protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
		QVToReconfiguration randomlySelected = QVToReconfiguration.empty();

		var randomValue = new Random().nextInt(2);
		if (isFilteringActivated) {
			if (randomValue == 0) {
				randomlySelected = deactivateFilter(options);
			}
		} else {
			if (randomValue == 0) {
				randomlySelected = activateFilter(options);
			}
		}

		return randomlySelected;
	}

}
