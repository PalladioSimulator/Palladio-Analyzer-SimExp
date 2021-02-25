package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static java.util.Objects.requireNonNull;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.ENERGY_CONSUMPTION_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_PACKET_LOSS;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.OPTIONS_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.PACKET_LOSS_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.STATE_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.filterMotesWithWirelessLinks;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.requirePcmSelfAdaptiveSystemState;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.retrieveDeltaIoTNetworkReconfiguration;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.retrieveDistributionFactorReconfiguration;

import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

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
		retrieveDeltaIoTNetworkReconfiguration(knowledge).setDistributionFactorValuesToDefaults();
		
		double energyConsumption = knowledge.getValue(ENERGY_CONSUMPTION_KEY).map(Double.class::cast).orElseThrow();
		if (isEnergyConsumptionIsViolated(energyConsumption)) {
			return planner.planEnergyConsumption(knowledge);	
		}
		return planner.planPacketLoss(knowledge);
	}

	@Override
	protected Reconfiguration<?> emptyReconfiguration() {
		return QVToReconfiguration.empty();
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

	private boolean isPacketLossViolated(double packetLoss) {
		return LOWER_PACKET_LOSS.isNotSatisfied(packetLoss);
	}

	private boolean isEnergyConsumptionIsViolated(double energyConsumtption) {
		return LOWER_ENERGY_CONSUMPTION.isNotSatisfied(energyConsumtption);
	}

}
