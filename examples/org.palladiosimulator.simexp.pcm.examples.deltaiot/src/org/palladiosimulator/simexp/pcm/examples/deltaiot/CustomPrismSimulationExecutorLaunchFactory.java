package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class CustomPrismSimulationExecutorLaunchFactory implements ILaunchFactory {

    @Override
    public boolean canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.CUSTOM) {
            return false;
        }
        SimulationEngine simulationEngine = config.getSimulationEngine();
        if (simulationEngine != SimulationEngine.PRISM) {
            return false;
        }

        return true;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory) {
        IPrismWorkflowConfiguration workflowConfiguration = (IPrismWorkflowConfiguration) config;
//        PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new DeltaIoTSimulationExecutorFactory(
                workflowConfiguration, modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider),
                seedProvider);
        return factory.create();
    }

}
