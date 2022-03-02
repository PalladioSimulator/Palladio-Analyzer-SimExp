package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.environment.DeltaIoTEnvironemtalDynamics.isSNRTemplate;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.environment.DeltaIoTEnvironemtalDynamics.toInputs;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTCommons {
	
	public final static String STATE_KEY = "PCM_STATE";
	public final static String OPTIONS_KEY = "OPTIONS";
	public final static String PACKET_LOSS_KEY = "PacketLoss";
	public final static String ENERGY_CONSUMPTION_KEY = "EnergyConsumption";
	public final static Threshold LOWER_PACKET_LOSS = Threshold.lessThan(0.12);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(31.4);
	public final static double DISTRIBUTION_FACTOR_INCREMENT = 0.1;
	public final static int TRANSMISSION_POWER_INCREMENT = 1;
	
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
	
	public static DeltaIoTNetworkReconfiguration retrieveDeltaIoTNetworkReconfiguration(SharedKnowledge knowledge) {
		return retrieveReconfiguration(DeltaIoTNetworkReconfiguration.class, findOptions(knowledge))
				.orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
	}
	
	public static DeltaIoTNetworkReconfiguration retrieveDeltaIoTNetworkReconfiguration(Set<QVToReconfiguration> options) {
		return retrieveReconfiguration(DeltaIoTNetworkReconfiguration.class, options)
				.orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
	}

	private static <T extends QVToReconfiguration> Optional<T> retrieveReconfiguration(Class<T> reconfClass,
			Set<QVToReconfiguration> options) {
		return options.stream()
				.filter(reconfClass::isInstance)
				.map(reconfClass::cast)
				.findFirst();
	}
	
	public static void requirePcmSelfAdaptiveSystemState(State source) {
		if (PcmSelfAdaptiveSystemState.class.isInstance(source) == false) {
			throw new RuntimeException("The self-adaptive system state is not a PCM-based.");
		}
	}
	
	public static Map<AssemblyContext, Map<LinkingResource, Double>> filterMotesWithWirelessLinks(
			PcmSelfAdaptiveSystemState state) {
		return filterLinksWithSNR(state).entrySet().stream()
				.collect(groupingBy(equalSourceMote(state), 
						mapping(Function.identity(), toMap(Map.Entry::getKey, Map.Entry::getValue))));
	}

	private static Map<LinkingResource, Double> filterLinksWithSNR(PcmSelfAdaptiveSystemState state) {
		return toInputs(state.getPerceivedEnvironmentalState().getValue().getValue()).stream()
				.filter(each -> isSNRTemplate(each.variable))
				.collect(toMap(k -> (LinkingResource) k.variable.getAppliedObjects().get(0), v -> getSNR(v)));
	}

	private static Double getSNR(InputValue input) {
		String value = CategoricalValue.class.cast(input.value).get();
		return Double.valueOf(value);
	}

	private static Function<Map.Entry<LinkingResource, Double>, AssemblyContext> equalSourceMote(
			PcmSelfAdaptiveSystemState state) {
		return entry -> DeltaIoTModelAccess.get().findSourceMote(entry.getKey(), state.getArchitecturalConfiguration());
	}
	
}
