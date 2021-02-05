package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics.isSNRTemplate;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics.toInputs;

import java.util.Map;
import java.util.function.Function;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTCommons {
	
	public final static String STATE_KEY = "PCM_STATE";
	public final static String OPTIONS_KEY = "OPTIONS";
	public final static String PACKET_LOSS_KEY = "PacketLoss";
	public final static String ENERGY_CONSUMPTION_KEY = "EnergyConsumption";
	public final static Threshold LOWER_PACKET_LOSS = Threshold.lessThan(0.1);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(0.4);
	public final static double DISTRIBUTION_FACTOR_INCREMENT = 0.1;
	public final static int TRANSMISSION_POWER_INCREMENT = 1;
	
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
