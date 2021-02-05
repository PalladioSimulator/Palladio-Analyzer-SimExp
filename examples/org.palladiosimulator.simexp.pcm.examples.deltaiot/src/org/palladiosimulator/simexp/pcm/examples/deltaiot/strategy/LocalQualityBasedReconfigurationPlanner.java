package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.ENERGY_CONSUMPTION_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.PACKET_LOSS_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.retrieveDistributionFactorReconfiguration;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.retrieveTransmissionPowerReconfiguration;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.MoteContextFilter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterCalculator;

public class LocalQualityBasedReconfigurationPlanner implements QualityBasedReconfigurationPlanner {

	public final static Threshold MEDIUM_PACKET_LOSS = Threshold.lessThan(0.3);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(0.4);
	
	private final ReconfigurationParameterCalculator paramCalculator;
	
	public LocalQualityBasedReconfigurationPlanner(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramCalculator = new ReconfigurationParameterCalculator(reconfParamsRepo);
	}

	@Override
	public QVToReconfiguration planPacketLoss(SharedKnowledge knowledge) {
		double packetLoss = knowledge.getValue(PACKET_LOSS_KEY).map(Double.class::cast).orElseThrow();
		if (MEDIUM_PACKET_LOSS.isSatisfied(packetLoss)) {
			return increaseDistributionLocally(knowledge);
		} else {
			return increaseTransmissionPowerLocally(knowledge);
		}
	}

	@Override
	public QVToReconfiguration planEnergyConsumption(SharedKnowledge knowledge) {
		double energyConsumtption = knowledge.getValue(ENERGY_CONSUMPTION_KEY).map(Double.class::cast).orElseThrow();
		if (LOWER_ENERGY_CONSUMPTION.isSatisfied(energyConsumtption)) {
			return decreaseDistributionLocally(knowledge);
		} else {
			return decreaseTransmissionPowerLocally(knowledge);
		}
	}

	private QVToReconfiguration increaseDistributionLocally(SharedKnowledge knowledge) {
		DistributionFactorReconfiguration dfReconfiguration = retrieveDistributionFactorReconfiguration(knowledge);

		var motesFilter = new MoteContextFilter(knowledge);
		for (MoteContext each : motesFilter.motesWithTwoLinks()) {
			var linkWithHighestSNR = motesFilter.linkWithHighestSNR(each);
			var adjustedFactors = paramCalculator.computeAdjustedDistributionFactors(linkWithHighestSNR, each);
			if (DistributionFactorReconfiguration.isValid(adjustedFactors)) {
				dfReconfiguration.adjustDistributionFactors(adjustedFactors);
			}
		}
		return dfReconfiguration;
	}
	
	private QVToReconfiguration decreaseDistributionLocally(SharedKnowledge knowledge) {
		DistributionFactorReconfiguration dfReconfiguration = retrieveDistributionFactorReconfiguration(knowledge);

		var motesFilter = new MoteContextFilter(knowledge);
		for (MoteContext each : motesFilter.motesWithTwoLinks()) {
			var linkWithHighestPower = motesFilter.linkWithHighestTransmissionPower(each);
			var adjustedFactors = paramCalculator.computeAdjustedDistributionFactors(linkWithHighestPower, each);
			if (DistributionFactorReconfiguration.isValid(adjustedFactors)) {
				dfReconfiguration.adjustDistributionFactors(adjustedFactors);
			}
		}
		return dfReconfiguration;
	}

	private QVToReconfiguration increaseTransmissionPowerLocally(SharedKnowledge knowledge) {
		TransmissionPowerReconfiguration tpReconfiguration = retrieveTransmissionPowerReconfiguration(knowledge);

		var motesWithLowSNRLinks = new MoteContextFilter(knowledge).motesWithSNRLowerThan(Threshold.lessThan(0));
		for (MoteContext each : motesWithLowSNRLinks.keySet()) {
			var lowSNRLink = motesWithLowSNRLinks.get(each);
			var adjustedPowerSettings = paramCalculator.computeIncreasedTransmissionPower(each.mote, lowSNRLink);
			if (tpReconfiguration.canBeAdjusted(adjustedPowerSettings)) {
				tpReconfiguration.adjustPowerSetting(adjustedPowerSettings);
			}
		}
		return tpReconfiguration;
	}

	private QVToReconfiguration decreaseTransmissionPowerLocally(SharedKnowledge knowledge) {
		TransmissionPowerReconfiguration tpReconfiguration = retrieveTransmissionPowerReconfiguration(knowledge);
				
		var motesWithHighSNRLinks = new MoteContextFilter(knowledge).motesWithSNRHigherThan(Threshold.greaterThanOrEqualTo(0));
		for (MoteContext each : motesWithHighSNRLinks.keySet()) {
			var highSNRLink = motesWithHighSNRLinks.get(each);
			var adjustedPowerSettings = paramCalculator.computeDecreasedTransmissionPower(each.mote, highSNRLink);
			if (tpReconfiguration.canBeAdjusted(adjustedPowerSettings)) {
				tpReconfiguration.adjustPowerSetting(adjustedPowerSettings);
			}
		}

		return tpReconfiguration;
	}

}
