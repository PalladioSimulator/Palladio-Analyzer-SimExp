package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

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

	private final ReconfigurationParameterCalculator paramCalculator;

	public LocalQualityBasedReconfigurationPlanner(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramCalculator = new ReconfigurationParameterCalculator(reconfParamsRepo);
	}

	@Override
	public QVToReconfiguration planPacketLoss(SharedKnowledge knowledge) {
		DistributionFactorReconfiguration dfReconfiguration = retrieveDistributionFactorReconfiguration(knowledge);
		if (increaseDistributionLocally(dfReconfiguration, knowledge)) {
			return dfReconfiguration;
		}
		
		TransmissionPowerReconfiguration tpReconfiguration = retrieveTransmissionPowerReconfiguration(knowledge);
		if (increaseTransmissionPowerLocally(tpReconfiguration, knowledge)) {
			return tpReconfiguration;
		}
		
		return QVToReconfiguration.empty();
	}

	@Override
	public QVToReconfiguration planEnergyConsumption(SharedKnowledge knowledge) {
		TransmissionPowerReconfiguration tpReconfiguration = retrieveTransmissionPowerReconfiguration(knowledge);
		if (decreaseTransmissionPowerLocally(tpReconfiguration, knowledge)) {
			return tpReconfiguration;
		}
		
		DistributionFactorReconfiguration dfReconfiguration = retrieveDistributionFactorReconfiguration(knowledge);
		if (decreaseDistributionLocally(dfReconfiguration, knowledge)) {
			return dfReconfiguration;
		}
		
		return QVToReconfiguration.empty();
	}

	private boolean increaseDistributionLocally(DistributionFactorReconfiguration dfReconfiguration, SharedKnowledge knowledge) {
		var isReconfigurable = false;
		
		var motesFilter = new MoteContextFilter(knowledge);
		for (MoteContext each : motesFilter.motesWithTwoLinks()) {
			var linkToDecrease = motesFilter.linkWithSmallestSNR(each);
			if (linkToDecrease.distributionFactor > 0) {
				var adjustedFactors = paramCalculator.computeAdjustedDistributionFactors(linkToDecrease, each);
				if (DistributionFactorReconfiguration.isValid(adjustedFactors)) {
					dfReconfiguration.adjustDistributionFactors(adjustedFactors);
					isReconfigurable = true;
				}
			}
		}
		
		return isReconfigurable;
	}

	private boolean decreaseDistributionLocally(DistributionFactorReconfiguration dfReconfiguration, SharedKnowledge knowledge) {
		var isReconfigurable = false;

		var motesFilter = new MoteContextFilter(knowledge);
		for (MoteContext each : motesFilter.motesWithTwoLinks()) {
			var linkToDecrease = motesFilter.linkWithHighestTransmissionPower(each);
			if (linkToDecrease.distributionFactor > 0) {
				var adjustedFactors = paramCalculator.computeAdjustedDistributionFactors(linkToDecrease, each);
				if (DistributionFactorReconfiguration.isValid(adjustedFactors)) {
					dfReconfiguration.adjustDistributionFactors(adjustedFactors);
					isReconfigurable = true;
				}
			}
		}
		
		return isReconfigurable;
	}

	private boolean increaseTransmissionPowerLocally(TransmissionPowerReconfiguration tpReconfiguration, SharedKnowledge knowledge) {
		var isReconfigurable = false;

		var motesWithLowSNRLinks = new MoteContextFilter(knowledge).motesWithSNRLowerThan(Threshold.lessThan(0));
		for (MoteContext each : motesWithLowSNRLinks.keySet()) {
			var lowSNRLink = motesWithLowSNRLinks.get(each);
			if (lowSNRLink.distributionFactor > 0 && lowSNRLink.transmissionPower < 15) {
				var adjustedPowerSettings = paramCalculator.computeIncreasedTransmissionPower(each.mote, lowSNRLink);
				if (tpReconfiguration.canBeAdjusted(adjustedPowerSettings)) {
					tpReconfiguration.adjustPowerSetting(adjustedPowerSettings);
					isReconfigurable = true;
				}
			}
		}
		
		return isReconfigurable;
	}

	private boolean decreaseTransmissionPowerLocally(TransmissionPowerReconfiguration tpReconfiguration, SharedKnowledge knowledge) {
		var isReconfigurable = false;

		var motesWithHighSNRLinks = new MoteContextFilter(knowledge)
				.motesWithSNRHigherThan(Threshold.greaterThanOrEqualTo(0));
		for (MoteContext each : motesWithHighSNRLinks.keySet()) {
			var highSNRLink = motesWithHighSNRLinks.get(each);
			if (highSNRLink.distributionFactor > 0 && highSNRLink.transmissionPower > 0) {
				var adjustedPowerSettings = paramCalculator.computeDecreasedTransmissionPower(each.mote, highSNRLink);
				if (tpReconfiguration.canBeAdjusted(adjustedPowerSettings)) {
					tpReconfiguration.adjustPowerSetting(adjustedPowerSettings);
					isReconfigurable = true;
				}
			}
		}

		return isReconfigurable;
	}

}
