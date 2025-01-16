package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class CustomLoadBalancingLaunchFactory implements ILaunchFactory {

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.CUSTOM) {
            return 0;
        }
        SimulationEngine simulationEngine = config.getSimulationEngine();
        if (simulationEngine != SimulationEngine.PCM) {
            return 0;
        }
        IPCMWorkflowConfiguration pcmWorkflowConfiguration = (IPCMWorkflowConfiguration) config;
        QualityObjective qualityObjective = pcmWorkflowConfiguration.getQualityObjective();
        if (qualityObjective != QualityObjective.PERFORMANCE) {
            return 0;
        }

        return 1;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory) {
        IPCMWorkflowConfiguration workflowConfiguration = (IPCMWorkflowConfiguration) config;
        SimulatedExperienceAccessor accessor = ServiceRegistry.get()
            .findService(SimulatedExperienceAccessor.class)
            .orElseThrow(() -> new RuntimeException(""));
        LoadBalancingSimulationExecutorFactory factory = new LoadBalancingSimulationExecutorFactory(
                workflowConfiguration, modelLoaderFactory,
                new SimulatedExperienceStore<>(accessor, descriptionProvider), seedProvider, accessor);
        return factory.create();
    }
}
