package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.OPTIONS_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.STATE_KEY;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public interface QualityBasedReconfigurationPlanner {
	
	public static PcmSelfAdaptiveSystemState findPcmState(SharedKnowledge knowledge) {
		return (PcmSelfAdaptiveSystemState) knowledge.getValue(STATE_KEY).orElseThrow();
	}
	
	public static Set<QVToReconfiguration> findOptions(SharedKnowledge knowledge) {
		Set<?> options = (Set<?>) knowledge.getValue(OPTIONS_KEY).orElseThrow();
		return options.stream()
				.filter(QVToReconfiguration.class::isInstance)
				.map(QVToReconfiguration.class::cast)
				.collect(Collectors.toSet());
	}
	
	public static DistributionFactorReconfiguration retrieveDistributionFactorReconfiguration(SharedKnowledge knowledge) {
		return retrieveReconfiguration(DistributionFactorReconfiguration.class, findOptions(knowledge))
				.orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
	}

	public static TransmissionPowerReconfiguration retrieveTransmissionPowerReconfiguration(SharedKnowledge knowledge) {
		return retrieveReconfiguration(TransmissionPowerReconfiguration.class, findOptions(knowledge))
				.orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
	}

	private static <T extends QVToReconfiguration> Optional<T> retrieveReconfiguration(Class<T> reconfClass,
			Set<QVToReconfiguration> options) {
		return options.stream()
				.filter(reconfClass::isInstance)
				.map(reconfClass::cast)
				.findFirst();
	}
	
	public QVToReconfiguration planPacketLoss(SharedKnowledge knowledge);

	public QVToReconfiguration planEnergyConsumption(SharedKnowledge knowledge);
}
