package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public interface QualityBasedReconfigurationPlanner {
		
	public QVToReconfiguration planPacketLoss(SharedKnowledge knowledge);

	public QVToReconfiguration planEnergyConsumption(SharedKnowledge knowledge);
}
