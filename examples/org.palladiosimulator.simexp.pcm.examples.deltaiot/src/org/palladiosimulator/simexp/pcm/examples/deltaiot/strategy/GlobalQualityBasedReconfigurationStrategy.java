package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public class GlobalQualityBasedReconfigurationStrategy extends DeltaIoTReconfigurationStrategy {

	private final static String ID = "GlobalQualityBasedReconfigurationStrategy";
	
	private GlobalQualityBasedReconfigurationStrategy() {
		super();
	}

	public static DeltaIoTReconfigurationStrategyBuilder newBuilder() {
		return new DeltaIoTReconfigurationStrategyBuilder(new GlobalQualityBasedReconfigurationStrategy());
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected Action<?> handlePacketLoss(PcmSelfAdaptiveSystemState state, SimulatedMeasurement packetLoss,
			Set<Action<?>> options) {
		DistributionFactorReconfiguration disFactorReconf = retrieveDistributionFactorReconfiguration(options);

		boolean canBeStillDistributed = false;
		canBeStillDistributed |= increaseDistributionFactorOfMote7(disFactorReconf);
		canBeStillDistributed |= increaseDistributionFactorOfMote10(disFactorReconf);
		canBeStillDistributed |= increaseDistributionFactorOfMote12(disFactorReconf);
		if (canBeStillDistributed) {
			return disFactorReconf;
		}

		TransmissionPowerReconfiguration transPowerReconf = retrieveTransmissionPowerReconfiguration(options);

		boolean canStillIncreaseLevel = false;
		for (String each : VARIABLE_REFERENCES) {
			canStillIncreaseLevel |= increaseTransmissionPower(each, transPowerReconf);
		}
		if (canStillIncreaseLevel) {
			return transPowerReconf;
		}

		return QVToReconfiguration.empty();
	}

	@Override
	protected Action<?> handleEnergyConsumption(PcmSelfAdaptiveSystemState state,
			SimulatedMeasurement energyConsumtption, Set<Action<?>> options) {
		TransmissionPowerReconfiguration transPowerReconf = retrieveTransmissionPowerReconfiguration(options);

		boolean canStillIncreaseLevel = false;
		for (String each : VARIABLE_REFERENCES) {
			canStillIncreaseLevel |= increaseTransmissionPower(each, transPowerReconf);
		}
		if (canStillIncreaseLevel) {
			return transPowerReconf;
		}

		DistributionFactorReconfiguration disFactorReconf = retrieveDistributionFactorReconfiguration(options);

		boolean canStillDecreaseDistribtion = false;
		canStillDecreaseDistribtion |= decreaseDistributionFactorOfMote7(disFactorReconf);
		canStillDecreaseDistribtion |= decreaseDistributionFactorOfMote10(disFactorReconf);
		canStillDecreaseDistribtion |= decreaseDistributionFactorOfMote12(disFactorReconf);
		if (canStillDecreaseDistribtion) {
			return disFactorReconf;
		}

		return QVToReconfiguration.empty();
	}

}
