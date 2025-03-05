package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.LocalQualityBasedReconfigurationPlanner;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

public class LocalQualityBasedMAPEKStrategyContext
        implements ReconfigurationStrategyContext<QVTOReconfigurator, QVToReconfiguration> {

    private final DeltaIoTReconfigurationStrategy2 strategy;
    private final DefaultDeltaIoTStrategyContext decoratedContext;

    public LocalQualityBasedMAPEKStrategyContext(PrismSimulatedMeasurementSpec packetLossSpec,
            PrismSimulatedMeasurementSpec energyConsumptionSpec,
            DeltaIoTReconfigurationParamRepository reconfParamsRepo, List<QVToReconfiguration> reconfigurations,
            IDeltaIoToReconfCustomizerFactory reconfCustomizerFactory,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker) {
        DeltaIoToReconfCustomizerResolver reconfCustomizerResolver = new DeltaIoToReconfCustomizerResolver();
        this.strategy = DeltaIoTReconfigurationStrategy2
            .newBuilder(modelAccess, simulationParameters, systemConfigurationTracker, reconfCustomizerResolver)
            .withID("LocalQualityBasedStrategy")
            .andPacketLossSpec(packetLossSpec)
            .andEnergyConsumptionSpec(energyConsumptionSpec)
            .andPlanner(new LocalQualityBasedReconfigurationPlanner(reconfParamsRepo, modelAccess,
                    reconfCustomizerResolver))
            .build();
        this.decoratedContext = new DefaultDeltaIoTStrategyContext(reconfParamsRepo, reconfigurations,
                reconfCustomizerFactory, modelAccess, simulationParameters, systemConfigurationTracker);
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurationSpace() {
        return decoratedContext.getReconfigurationSpace();
    }

    @Override
    public boolean isSelectionPolicy() {
        return false;
    }

    @Override
    public ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> getStrategy() {
        return strategy;
    }

    @Override
    public Policy<QVTOReconfigurator, QVToReconfiguration> getSelectionPolicy() {
        return null;
    }

}
