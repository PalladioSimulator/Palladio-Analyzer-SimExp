package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.performance.ModelledPerformancePcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledPerformancePcmExperienceSimulationExecutorLaunchFactory implements ILaunchFactory {

    @Override
    public boolean canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.MODELLED) {
            return false;
        }
        SimulationEngine simulationEngine = config.getSimulationEngine();
        if (simulationEngine != SimulationEngine.PCM) {
            return false;
        }
        IPCMWorkflowConfiguration pcmWorkflowConfiguration = (IPCMWorkflowConfiguration) config;
        QualityObjective qualityObjective = pcmWorkflowConfiguration.getQualityObjective();
        if (qualityObjective != QualityObjective.PERFORMANCE) {
            return false;
        }

        return true;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory) {
        IModelledPcmWorkflowConfiguration workflowConfiguration = (IModelledPcmWorkflowConfiguration) config;
        ModelledModelLoader.Factory modelledModelLoaderFactory = (ModelledModelLoader.Factory) modelLoaderFactory;
        ModelledPerformancePcmExperienceSimulationExecutorFactory factory = new ModelledPerformancePcmExperienceSimulationExecutorFactory(
                workflowConfiguration, modelledModelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider),
                seedProvider);
        return factory.create();
    }

}
