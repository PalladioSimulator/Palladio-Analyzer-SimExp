package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public class NonAdaptiveStrategy implements Policy<QVToReconfiguration> {

	@Override
	public String getId() {
		return "NonAdaptiveStrategy";
	}

	@Override
	public QVToReconfiguration select(State<QVToReconfiguration> source, Set<QVToReconfiguration> options) {
		return QVToReconfiguration.empty();
	}

}
