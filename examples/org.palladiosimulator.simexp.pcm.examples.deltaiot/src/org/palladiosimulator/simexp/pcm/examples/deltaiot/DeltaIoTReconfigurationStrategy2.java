package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics.isSNRTemplate;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics.toInputs;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import com.google.common.collect.Maps;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTReconfigurationStrategy2 extends ReconfigurationStrategy {

	public static class DeltaIoTReconfigurationStrategy2Builder {

		private String id;
		private QualityBasedReconfigurationPlanner planner;
		private PrismSimulatedMeasurementSpec packetLossSpec;
		private PrismSimulatedMeasurementSpec energyConsumptionSpec;

		public DeltaIoTReconfigurationStrategy2Builder withID(String id) {
			this.id = id;
			return this;
		}
		
		public DeltaIoTReconfigurationStrategy2Builder andPacketLossSpec(PrismSimulatedMeasurementSpec packetLossSpec) {
			this.packetLossSpec = packetLossSpec;
			return this;
		}

		public DeltaIoTReconfigurationStrategy2Builder andEnergyConsumptionSpec(
				PrismSimulatedMeasurementSpec energyConsumptionSpec) {
			this.energyConsumptionSpec = energyConsumptionSpec;
			return this;
		}
		
		public DeltaIoTReconfigurationStrategy2Builder andPlanner(
				QualityBasedReconfigurationPlanner planner) {
			this.planner = planner;
			return this;
		}

		public DeltaIoTReconfigurationStrategy2 build() {
			requireNonNull(id, "ID must be specified.");
			if (id.isBlank()) {
				throw new IllegalArgumentException("ID is not properly specified.");
			}
			requireNonNull(packetLossSpec, "Packet loss spec is missing");
			requireNonNull(energyConsumptionSpec, "Energy consumption spec is missing");
			requireNonNull(planner, "Planner is missing.");

			return new DeltaIoTReconfigurationStrategy2(id, planner, packetLossSpec, energyConsumptionSpec);
		}

	}
	
	protected static class MoteContext {
		
		protected static class LinkingResourceQuantity {
			
			public final double SNR;
			public final double transmissionPower;
			
			public LinkingResourceQuantity(double SNR, double transmissionPower) {
				this.SNR = SNR;
				this.transmissionPower = transmissionPower;
			}
		}
		
		public final AssemblyContext mote;
		public final Map<LinkingResource, LinkingResourceQuantity> linkDetails;
		
		public MoteContext(AssemblyContext mote, Map<LinkingResource, Double> linkToSNR) {
			this.mote = mote;
			this.linkDetails = Maps.newHashMap();
			
			initLinkDetails(mote, linkToSNR);
		}
		
		private Map<LinkingResource, LinkingResourceQuantity> initLinkDetails(AssemblyContext mote, 
				Map<LinkingResource, Double> linkToSNR) {
			for (LinkingResource each : linkToSNR.keySet()) {
				var SNR = linkToSNR.get(each);
				var transmissionPower = DeltaIoTModelAccess.get().retrieveTransmissionPower(mote, each);
				linkDetails.put(each, new LinkingResourceQuantity(SNR, transmissionPower));
			}
			return linkDetails;
		}
		
		public boolean hasTwoLinks() {
			return linkDetails.size() == 2;
		}

		public String getId() {
			return mote.getId();
		}
		
	}
	
	public final static String STATE_KEY = "PCM_STATE";
	public final static String OPTIONS_KEY = "OPTIONS";
	public final static String PACKET_LOSS_KEY = "PacketLoss";
	public final static String ENERGY_CONSUMPTION_KEY = "EnergyConsumption";
	public final static Threshold LOWER_PACKET_LOSS = Threshold.lessThan(0.1);
	public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(0.4);
	public final static double DISTRIBUTION_FACTOR_INCREMENT = 0.1;
	public final static int TRANSMISSION_POWER_INCREMENT = 1;

	private final String id;
	private final QualityBasedReconfigurationPlanner planner;
	private final PrismSimulatedMeasurementSpec packetLossSpec;
	private final PrismSimulatedMeasurementSpec energyConsumptionSpec;

	private DeltaIoTReconfigurationStrategy2(String id, QualityBasedReconfigurationPlanner planner,
			PrismSimulatedMeasurementSpec packetLossSpec, PrismSimulatedMeasurementSpec energyConsumptionSpec) {
		this.id = id;
		this.planner = planner;
		this.packetLossSpec = packetLossSpec;
		this.energyConsumptionSpec = energyConsumptionSpec;
	}
	
	public static DeltaIoTReconfigurationStrategy2Builder newBuilder() {
		return new DeltaIoTReconfigurationStrategy2Builder();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	protected SharedKnowledge monitor(State source, Set<Reconfiguration<?>> options) {
		requirePcmSelfAdaptiveSystemState(source);

		PcmSelfAdaptiveSystemState state = PcmSelfAdaptiveSystemState.class.cast(source);

		SharedKnowledge knowledge = new SharedKnowledge();
		knowledge.store(STATE_KEY, state);
		knowledge.store(OPTIONS_KEY, options);

		addMonitoredEnvironmentValues(state, knowledge);
		addMonitoredQualityValues(state, knowledge);

		return knowledge;
	}

	@Override
	protected boolean analyse(SharedKnowledge knowledge) {
		double packetLoss = knowledge.getValue(PACKET_LOSS_KEY).map(Double.class::cast).orElseThrow();
		if (isPacketLossViolated(packetLoss)) {
			return true;
		}

		double energyConsumption = knowledge.getValue(ENERGY_CONSUMPTION_KEY).map(Double.class::cast).orElseThrow();
		if (isEnergyConsumptionIsViolated(energyConsumption)) {
			return true;
		}

		return false;
	}

	@Override
	protected Reconfiguration<?> plan(SharedKnowledge knowledge) {
		double packetLoss = knowledge.getValue(PACKET_LOSS_KEY).map(Double.class::cast).orElseThrow();
		if (isPacketLossViolated(packetLoss)) {
			return planner.planPacketLoss(knowledge);
		}
		return planner.planEnergyConsumption(knowledge);
	}

	@Override
	protected Reconfiguration<?> emptyReconfiguration() {
		return QVToReconfiguration.empty();
	}

	private void requirePcmSelfAdaptiveSystemState(State source) {
		if (PcmSelfAdaptiveSystemState.class.isInstance(source) == false) {
			throw new RuntimeException("The self-adaptive system state is not a PCM-based.");
		}
	}

	private void addMonitoredEnvironmentValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
		var motesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : motesToLinks.keySet()) {
			var moteContext = new MoteContext(each, motesToLinks.get(each));
			knowledge.store(moteContext.getId(), moteContext);
		}
	}

	private void addMonitoredQualityValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
		SimulatedMeasurement packetLoss = state.getQuantifiedState().findMeasurementWith(packetLossSpec)
				.orElseThrow(() -> new RuntimeException(
						String.format("There is no simulated measurement for spec %s", packetLossSpec.getName())));
		knowledge.store(PACKET_LOSS_KEY, packetLoss.getValue());

		SimulatedMeasurement energyConsumtption = state.getQuantifiedState().findMeasurementWith(energyConsumptionSpec)
				.orElseThrow(() -> new RuntimeException(String.format("There is no simulated measurement for spec %s",
						energyConsumptionSpec.getName())));
		knowledge.store(ENERGY_CONSUMPTION_KEY, energyConsumtption.getValue());
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

	private boolean isPacketLossViolated(double packetLoss) {
		return LOWER_PACKET_LOSS.isNotSatisfied(packetLoss);
	}

	private boolean isEnergyConsumptionIsViolated(double energyConsumtption) {
		return LOWER_ENERGY_CONSUMPTION.isNotSatisfied(energyConsumtption);
	}

}
