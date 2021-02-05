package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.environment.DeltaIoTEnvironemtalDynamics.isSNRTemplate;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.environment.DeltaIoTEnvironemtalDynamics.toInputs;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class LocalQualityBasedReconfigurationStrategy extends DeltaIoTReconfigurationStrategy {
    
    private static final Logger LOGGER = Logger.getLogger(LocalQualityBasedReconfigurationStrategy.class.getName());

	private static class WirelessLinkFilter {

		private final Map<LinkingResource, Double> linksWithSNRValue;

		public WirelessLinkFilter(Map<LinkingResource, Double> linksWithSNRValue) {
			this.linksWithSNRValue = linksWithSNRValue;
		}

		public List<LinkingResource> linksWithSNRLowerThan(Threshold lowerBound) {
			return orderBySNRValue(linksWithSNRValue).stream()
					.takeWhile(each -> lowerBound.isSatisfied(each.getValue())).map(each -> each.getKey())
					.collect(toList());
		}

		public List<LinkingResource> linksWithSNRHigherThan(Threshold upperBound) {
			return orderBySNRValue(linksWithSNRValue).stream()
					.dropWhile(each -> upperBound.isNotSatisfied(each.getValue())).map(each -> each.getKey())
					.collect(toList());
		}

		public LinkingResource linkWithHighestSNR() {
			return orderBySNRValue(linksWithSNRValue).get(0).getKey();
		}

		private List<Entry<LinkingResource, Double>> orderBySNRValue(Map<LinkingResource, Double> links) {
			return links.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(toList());
		}

	}

	private final static String ID = "LocalQualityBasedReconfigurationStrategy";
	public final static Threshold MEDIUM_PACKET_LOSS = Threshold.lessThan(0.3);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(0.4);

	public static DeltaIoTReconfigurationStrategyBuilder newBuilder() {
		return new DeltaIoTReconfigurationStrategyBuilder(new LocalQualityBasedReconfigurationStrategy());
	}	

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected Action<?> handlePacketLoss(PcmSelfAdaptiveSystemState state, SimulatedMeasurement packetLoss,
			Set<Action<?>> options) {
		LOGGER.info("Start with actions selection.");
		long start = System.currentTimeMillis();
		
		Action<?> action;
		if (MEDIUM_PACKET_LOSS.isSatisfied(packetLoss.getValue())) {
			action = increaseDistribution(state, options);
		} else {
			action = increaseTransmissionPower(state, options);
		}
		
		long end = System.currentTimeMillis();
		
		LOGGER.info("Stop with action selection, took : " + ((end - start) / 1000));
		
		return action;
	}

	@Override
	protected Action<?> handleEnergyConsumption(PcmSelfAdaptiveSystemState state,
			SimulatedMeasurement energyConsumtption, Set<Action<?>> options) {
		if (LOWER_ENERGY_CONSUMPTION.isSatisfied(energyConsumtption.getValue())) {
			return decreaseDistribution(state, options);
		} else {
			return decreaseTransmissionPower(state, options);
		}
	}

	private Action<?> decreaseDistribution(PcmSelfAdaptiveSystemState state, Set<Action<?>> options) {
		DistributionFactorReconfiguration disFactorReconf = retrieveDistributionFactorReconfiguration(options);

		Map<AssemblyContext, Map<LinkingResource, Double>> sourceMotesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : sourceMotesToLinks.keySet()) {
			Map<LinkingResource, Double> links = sourceMotesToLinks.get(each);
			if (links.size() == 2) {
				decreaseDistributionFactorWithHighestTransmissionPower(each, links, disFactorReconf);
			}
		}

		return disFactorReconf;
	}

	private Action<?> decreaseTransmissionPower(PcmSelfAdaptiveSystemState state, Set<Action<?>> options) {
		TransmissionPowerReconfiguration transPowerReconf = retrieveTransmissionPowerReconfiguration(options);

		Map<AssemblyContext, Map<LinkingResource, Double>> sourceMotesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : sourceMotesToLinks.keySet()) {
			WirelessLinkFilter linkFilter = new WirelessLinkFilter(sourceMotesToLinks.get(each));
			linkFilter.linksWithSNRHigherThan(Threshold.greaterThanOrEqualTo(0))
					.forEach(link -> decreaseTransmissionPower(each, link, transPowerReconf));
		}

		return transPowerReconf;
	}

	private Action<?> increaseDistribution(PcmSelfAdaptiveSystemState state, Set<Action<?>> options) {
		DistributionFactorReconfiguration disFactorReconf = retrieveDistributionFactorReconfiguration(options);

		Map<AssemblyContext, Map<LinkingResource, Double>> sourceMotesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : sourceMotesToLinks.keySet()) {
			Map<LinkingResource, Double> links = sourceMotesToLinks.get(each);
			if (links.size() == 2) {
				increaseDistributionFactorWithHighestSNR(each, links, disFactorReconf);
			}
		}

		return disFactorReconf;
	}

	private Action<?> increaseTransmissionPower(PcmSelfAdaptiveSystemState state, Set<Action<?>> options) {
		TransmissionPowerReconfiguration transPowerReconf = retrieveTransmissionPowerReconfiguration(options);

		Map<AssemblyContext, Map<LinkingResource, Double>> sourceMotesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : sourceMotesToLinks.keySet()) {
			WirelessLinkFilter linkFilter = new WirelessLinkFilter(sourceMotesToLinks.get(each));
			linkFilter.linksWithSNRLowerThan(Threshold.lessThan(0))
					.forEach(link -> increaseTransmissionPower(each, link, transPowerReconf));
		}

		return transPowerReconf;
	}

	private void decreaseTransmissionPower(AssemblyContext sourceMote, LinkingResource link,
			TransmissionPowerReconfiguration transPowerReconf) {
		List<VariableUsage> varUsages = sourceMote.getConfigParameterUsages__AssemblyContext();
		for (VariableUsage each : varUsages) {
			if (DeltaIoTModelAccess.get().isTransmissionPowerOfLink(each, link)) {
				decreaseTransmissionPower(each.getNamedReference__VariableUsage().getReferenceName(), transPowerReconf);
			}
		}
	}

	private void increaseTransmissionPower(AssemblyContext sourceMote, LinkingResource link,
			TransmissionPowerReconfiguration transPowerReconf) {
		List<VariableUsage> varUsages = sourceMote.getConfigParameterUsages__AssemblyContext();
		for (VariableUsage each : varUsages) {
			if (DeltaIoTModelAccess.get().isTransmissionPowerOfLink(each, link)) {
				increaseTransmissionPower(each.getNamedReference__VariableUsage().getReferenceName(), transPowerReconf);
			}
		}
	}

	private void increaseDistributionFactorWithHighestSNR(AssemblyContext sourceMote,
			Map<LinkingResource, Double> links, DistributionFactorReconfiguration disFactorReconf) {
		List<ProbabilisticBranchTransition> branchesToAdapt = DeltaIoTModelAccess.get()
				.retrieveCommunicatingBranches(sourceMote);
		LinkingResource linkWithHighestSNR = new WirelessLinkFilter(links).linkWithHighestSNR();

		ProbabilisticBranchTransition branchToIncrease;
		ProbabilisticBranchTransition branchToDecrease;
		if (isPhysicalLink(branchesToAdapt.get(0), linkWithHighestSNR)) {
			branchToIncrease = branchesToAdapt.get(1);
			branchToDecrease = branchesToAdapt.get(0);
		} else {
			branchToIncrease = branchesToAdapt.get(0);
			branchToDecrease = branchesToAdapt.get(1);
		}

		increaseDistributionFactor(branchToIncrease, branchToDecrease, disFactorReconf);
	}

	private void decreaseDistributionFactorWithHighestTransmissionPower(AssemblyContext sourceMote,
			Map<LinkingResource, Double> links, DistributionFactorReconfiguration disFactorReconf) {
		Iterator<Entry<LinkingResource, Double>> iterator = links.entrySet().iterator();
		LinkingResource link1 = iterator.next().getKey();
		LinkingResource link2 = iterator.next().getKey();

		double transmissionPower1 = DeltaIoTModelAccess.get().retrieveTransmissionPower(sourceMote, link1);
		double transmissionPower2 = DeltaIoTModelAccess.get().retrieveTransmissionPower(sourceMote, link2);

		List<ProbabilisticBranchTransition> branchesToAdapt = DeltaIoTModelAccess.get().retrieveCommunicatingBranches(sourceMote);

		ProbabilisticBranchTransition branchToIncrease;
		ProbabilisticBranchTransition branchToDecrease;
		if (transmissionPower1 > transmissionPower2) {
			branchToIncrease = isPhysicalLink(branchesToAdapt.get(0), link2) ? branchesToAdapt.get(0)
					: branchesToAdapt.get(1);
			branchToDecrease = isPhysicalLink(branchesToAdapt.get(0), link1) ? branchesToAdapt.get(0)
					: branchesToAdapt.get(1);
		} else {
			branchToIncrease = isPhysicalLink(branchesToAdapt.get(0), link1) ? branchesToAdapt.get(0)
					: branchesToAdapt.get(1);
			branchToDecrease = isPhysicalLink(branchesToAdapt.get(0), link2) ? branchesToAdapt.get(0)
					: branchesToAdapt.get(1);
		}

		increaseDistributionFactor(branchToIncrease, branchToDecrease, disFactorReconf);
	}

	private boolean isPhysicalLink(ProbabilisticBranchTransition probabilisticBranchTransition,
			LinkingResource physicalLink) {
		String usedLinkId = probabilisticBranchTransition.getEntityName()
				.substring(probabilisticBranchTransition.getEntityName().length() - 1);
		return physicalLink.getEntityName().endsWith(usedLinkId);
	}

	private Map<AssemblyContext, Map<LinkingResource, Double>> filterMotesWithWirelessLinks(
			PcmSelfAdaptiveSystemState state) {
		return filterLinksWithSNR(state).entrySet().stream().collect(groupingBy(equalSourceMote(state),
				mapping(Function.identity(), toMap(Map.Entry::getKey, Map.Entry::getValue))));
	}

	private Map<LinkingResource, Double> filterLinksWithSNR(PcmSelfAdaptiveSystemState state) {
		return toInputs(state.getPerceivedEnvironmentalState().getValue().getValue()).stream()
				.filter(each -> isSNRTemplate(each.variable)).collect(toMap(
						k -> (LinkingResource) k.variable.getAppliedObjects().get(0), v -> getSNR(v)));
	}

	private Double getSNR(InputValue input) {
		String value = CategoricalValue.class.cast(input.value).get();
		return Double.valueOf(value);
	}

	private Function<Map.Entry<LinkingResource, Double>, AssemblyContext> equalSourceMote(
			PcmSelfAdaptiveSystemState state) {
		return entry -> DeltaIoTModelAccess.get().findSourceMote(entry.getKey(), state.getArchitecturalConfiguration());
	}

}
