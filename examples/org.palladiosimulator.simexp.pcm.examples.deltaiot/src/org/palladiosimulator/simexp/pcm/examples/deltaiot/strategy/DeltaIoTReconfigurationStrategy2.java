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

import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSampleLogger;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

public class DeltaIoTReconfigurationStrategy2 extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

    public static class DeltaIoTReconfigurationStrategy2Builder {
        private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;
        private final SimulationParameters simulationParameters;
        private final SystemConfigurationTracker systemConfigurationTracker;
        private final IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver;

        private String id;
        private QualityBasedReconfigurationPlanner planner;
        private PrismSimulatedMeasurementSpec packetLossSpec;
        private PrismSimulatedMeasurementSpec energyConsumptionSpec;

        public DeltaIoTReconfigurationStrategy2Builder(DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess,
                SimulationParameters simulationParameters, SystemConfigurationTracker systemConfigurationTracker,
                IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver) {
            this.modelAccess = modelAccess;
            this.simulationParameters = simulationParameters;
            this.systemConfigurationTracker = systemConfigurationTracker;
            this.reconfCustomizerResolver = reconfCustomizerResolver;
        }

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

        public DeltaIoTReconfigurationStrategy2Builder andPlanner(QualityBasedReconfigurationPlanner planner) {
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

            return new DeltaIoTReconfigurationStrategy2(id, planner, packetLossSpec, energyConsumptionSpec, modelAccess,
                    simulationParameters, systemConfigurationTracker, reconfCustomizerResolver);
        }

    }

    private final String id;
    private final QualityBasedReconfigurationPlanner planner;
    private final PrismSimulatedMeasurementSpec packetLossSpec;
    private final PrismSimulatedMeasurementSpec energyConsumptionSpec;
    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;
    private final SimulationParameters simulationParameters;
    private final SystemConfigurationTracker systemConfigurationTracker;
    private final IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver;

    private DeltaIoTReconfigurationStrategy2(String id, QualityBasedReconfigurationPlanner planner,
            PrismSimulatedMeasurementSpec packetLossSpec, PrismSimulatedMeasurementSpec energyConsumptionSpec,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker,
            IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver) {
        super(new DeltaIoTSampleLogger(modelAccess));
        this.id = id;
        this.planner = planner;
        this.packetLossSpec = packetLossSpec;
        this.energyConsumptionSpec = energyConsumptionSpec;
        this.modelAccess = modelAccess;
        this.simulationParameters = simulationParameters;
        this.systemConfigurationTracker = systemConfigurationTracker;
        this.reconfCustomizerResolver = reconfCustomizerResolver;
    }

    public static DeltaIoTReconfigurationStrategy2Builder newBuilder(
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker,
            IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver) {
        return new DeltaIoTReconfigurationStrategy2Builder(modelAccess, simulationParameters,
                systemConfigurationTracker, reconfCustomizerResolver);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        systemConfigurationTracker.prepareNetworkConfig();

        requirePcmSelfAdaptiveSystemState(source);

        PcmSelfAdaptiveSystemState state = PcmSelfAdaptiveSystemState.class.cast(source);

        knowledge.store(STATE_KEY, state);

        addMonitoredEnvironmentValues(state, knowledge);
        addMonitoredQualityValues(state, knowledge);

        systemConfigurationTracker.processNetworkConfig(knowledge);
    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        double packetLoss = knowledge.getValue(PACKET_LOSS_KEY)
            .map(Double.class::cast)
            .orElseThrow();
        double energyConsumption = knowledge.getValue(ENERGY_CONSUMPTION_KEY)
            .map(Double.class::cast)
            .orElseThrow();
        return isPacketLossViolated(packetLoss) || isEnergyConsumptionViolated(energyConsumption);
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        IDeltaIoToReconfiguration deltaIoTReconfiguration = reconfCustomizerResolver
            .resolveDeltaIoTReconfCustomizer(options);
        if (deltaIoTReconfiguration instanceof IDistributionFactorReconfiguration) {
            IDistributionFactorReconfiguration distributionFactorReconfiguration = (IDistributionFactorReconfiguration) deltaIoTReconfiguration;
            distributionFactorReconfiguration.setDistributionFactorValuesToDefaults();
        }
        knowledge.store(OPTIONS_KEY, options);

        double energyConsumption = knowledge.getValue(ENERGY_CONSUMPTION_KEY)
            .map(Double.class::cast)
            .orElseThrow();
        if (isEnergyConsumptionViolated(energyConsumption)) {
            return planner.planEnergyConsumption(knowledge);
        }
        return planner.planPacketLoss(knowledge);
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return EmptyQVToReconfiguration.empty();
    }

    private void addMonitoredEnvironmentValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
        var motesToLinks = filterMotesWithWirelessLinks(modelAccess, state);
        for (AssemblyContext each : motesToLinks.keySet()) {
            var moteContext = new MoteContext(modelAccess, each, motesToLinks.get(each));
            knowledge.store(moteContext.getId(), moteContext);
        }
    }

    private void addMonitoredQualityValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
        SimulatedMeasurement packetLoss = state.getQuantifiedState()
            .findMeasurementWith(packetLossSpec)
            .orElseThrow(() -> new RuntimeException(
                    String.format("There is no simulated measurement for spec %s", packetLossSpec.getName())));
        knowledge.store(PACKET_LOSS_KEY, packetLoss.getValue());

        SimulatedMeasurement energyConsumtption = state.getQuantifiedState()
            .findMeasurementWith(energyConsumptionSpec)
            .orElseThrow(() -> new RuntimeException(
                    String.format("There is no simulated measurement for spec %s", energyConsumptionSpec.getName())));
        knowledge.store(ENERGY_CONSUMPTION_KEY, energyConsumtption.getValue());
    }

    private boolean isPacketLossViolated(double packetLoss) {
        return LOWER_PACKET_LOSS.isNotSatisfied(packetLoss);
    }

    private boolean isEnergyConsumptionViolated(double energyConsumtption) {
        return LOWER_ENERGY_CONSUMPTION.isNotSatisfied(energyConsumtption);
    }

}
