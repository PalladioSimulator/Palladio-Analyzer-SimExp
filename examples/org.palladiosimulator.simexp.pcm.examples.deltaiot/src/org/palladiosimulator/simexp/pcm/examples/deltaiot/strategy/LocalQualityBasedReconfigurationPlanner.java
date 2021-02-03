package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2.DISTRIBUTION_FACTOR_INCREMENT;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2.ENERGY_CONSUMPTION_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2.PACKET_LOSS_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2.TRANSMISSION_POWER_INCREMENT;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.QualityBasedReconfigurationPlanner.retrieveDistributionFactorReconfiguration;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.QualityBasedReconfigurationPlanner.retrieveTransmissionPowerReconfiguration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2.MoteContext;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2.MoteContext.LinkingResourceQuantity;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterManager;

import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.VariableReference;

public class LocalQualityBasedReconfigurationPlanner implements QualityBasedReconfigurationPlanner {

	private static class MoteContextFilter {

		private final List<MoteContext> contexts;

		public MoteContextFilter(SharedKnowledge knowledge) {
			this.contexts = knowledge.getValues().stream()
					.filter(MoteContext.class::isInstance)
					.map(MoteContext.class::cast)
					.collect(toList());
		}

		public List<MoteContext> motesWithTwoLinks() {
			return contexts.stream()
					.filter(MoteContext::hasTwoLinks)
					.collect(toList());
		}

		public Map<MoteContext, LinkingResource> motesWithSNRLowerThan(Threshold lowerBound) {
			Map<MoteContext, LinkingResource> result = Maps.newHashMap();
			for (MoteContext each : contexts) {
				linksWithSNRLowerThan(lowerBound, each).forEach(link -> result.put(each, link));
			}
			return result;
		}
		
		public Map<MoteContext, LinkingResource> motesWithSNRHigherThan(Threshold lowerBound) {
			Map<MoteContext, LinkingResource> result = Maps.newHashMap();
			for (MoteContext each : contexts) {
				linksWithSNRHigherThan(lowerBound, each).forEach(link -> result.put(each, link));
			}
			return result;
		}

		public LinkingResource linkWithHighestSNR(MoteContext mote) {
			return orderBySNRValue(mote.linkDetails).get(0).getKey();
		}
		
		public LinkingResource linkWithHighestTransmissionPower(MoteContext mote) {
			return orderByTransmissionPowerValue(mote.linkDetails).get(0).getKey();
		}
		
		private List<LinkingResource> linksWithSNRLowerThan(Threshold lowerBound, MoteContext mote) {
			return orderBySNRValue(mote.linkDetails).stream()
					.takeWhile(each -> lowerBound.isSatisfied(each.getValue()))
					.map(each -> each.getKey())
					.collect(toList());
		}
		
		private List<LinkingResource> linksWithSNRHigherThan(Threshold upperBound, MoteContext mote) {
			return orderBySNRValue(mote.linkDetails).stream()
					.dropWhile(each -> upperBound.isNotSatisfied(each.getValue()))
					.map(each -> each.getKey())
					.collect(toList());
		}

		private List<Entry<LinkingResource, Double>> orderBySNRValue(
				Map<LinkingResource, LinkingResourceQuantity> links) {
			return links.entrySet().stream()
					.map(e -> Map.entry(e.getKey(), e.getValue().SNR))
					.sorted(Map.Entry.comparingByValue())
					.collect(toList());
		}
		
		private List<Entry<LinkingResource, Double>> orderByTransmissionPowerValue(
				Map<LinkingResource, LinkingResourceQuantity> links) {
			return links.entrySet().stream()
					.map(e -> Map.entry(e.getKey(), e.getValue().transmissionPower))
					.sorted(Map.Entry.comparingByValue())
					.collect(toList());
		}

	}

	public final static Threshold MEDIUM_PACKET_LOSS = Threshold.lessThan(0.3);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(0.4);
	
	private final ReconfigurationParameterManager paramManager;
	
	public LocalQualityBasedReconfigurationPlanner(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramManager = new ReconfigurationParameterManager(reconfParamsRepo);
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
			var adjustedFactors = computeAdjustedDistributionFactors(linkWithHighestSNR, each);
			if (dfReconfiguration.isValid(adjustedFactors)) {
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
			var adjustedFactors = computeAdjustedDistributionFactors(linkWithHighestPower, each);
			if (dfReconfiguration.isValid(adjustedFactors)) {
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
			var adjustedPowerSettings = increaseTransmissionPower(each.mote, lowSNRLink);
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
			var adjustedPowerSettings = decreaseTransmissionPower(each.mote, highSNRLink);
			if (tpReconfiguration.canBeAdjusted(adjustedPowerSettings)) {
				tpReconfiguration.adjustPowerSetting(adjustedPowerSettings);
			}
		}

		return tpReconfiguration;
	}

	private Map<ProbabilisticBranchTransition, Double> computeAdjustedDistributionFactors(
			LinkingResource linkWithHighestQuantity, MoteContext context) {
		var branchesToAdapt = DeltaIoTModelAccess.get().retrieveCommunicatingBranches(context.mote);

		ProbabilisticBranchTransition branchToIncrease;
		ProbabilisticBranchTransition branchToDecrease;
		if (isPhysicalLink(branchesToAdapt.get(0), linkWithHighestQuantity)) {
			branchToIncrease = paramManager.findBranchWith(branchesToAdapt.get(1).getEntityName());
			branchToDecrease = paramManager.findBranchWith(branchesToAdapt.get(0).getEntityName());
		} else {
			branchToIncrease = paramManager.findBranchWith(branchesToAdapt.get(0).getEntityName());
			branchToDecrease = paramManager.findBranchWith(branchesToAdapt.get(1).getEntityName());
		}

		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(branchToDecrease, DISTRIBUTION_FACTOR_INCREMENT * (-1));
		factors.put(branchToIncrease, DISTRIBUTION_FACTOR_INCREMENT);
		return factors;
	}
	
	private Map<VariableReference, Integer> increaseTransmissionPower(AssemblyContext mote, LinkingResource link) {
		return computeAdjustedTransmissionPower(mote, link, TRANSMISSION_POWER_INCREMENT);
	}
	
	private Map<VariableReference, Integer> decreaseTransmissionPower(AssemblyContext mote, LinkingResource link) {
		return computeAdjustedTransmissionPower(mote, link, TRANSMISSION_POWER_INCREMENT * (-1));
	}
	
	private Map<VariableReference, Integer> computeAdjustedTransmissionPower(AssemblyContext mote, LinkingResource link, int adjustement) {
		Map<VariableReference, Integer> powerSettings = Maps.newHashMap();
		
		List<VariableUsage> varUsages = mote.getConfigParameterUsages__AssemblyContext();
		for (VariableUsage each : varUsages) {
			if (DeltaIoTModelAccess.get().isTransmissionPowerOfLink(each, link)) {
				var varRef = each.getNamedReference__VariableUsage().getReferenceName();
				powerSettings.put(paramManager.findVariableReferenceWith(varRef), adjustement);
			}
		}
		return powerSettings;
	}

	private boolean isPhysicalLink(ProbabilisticBranchTransition probabilisticBranchTransition,
			LinkingResource physicalLink) {
		String usedLinkId = probabilisticBranchTransition.getEntityName()
				.substring(probabilisticBranchTransition.getEntityName().length() - 1);
		return physicalLink.getEntityName().endsWith(usedLinkId);
	}

}
