package org.palladiosimulator.simexp.pcm.examples.hri;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.reliability.ModelledReliabilityPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledReliabilityPcmExperienceSimulationExecutorLaunchFactory implements ILaunchFactory {

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.MODELLED) {
            return 0;
        }
        SimulationEngine simulationEngine = config.getSimulationEngine();
        if (simulationEngine != SimulationEngine.PCM) {
            return 0;
        }
        IPCMWorkflowConfiguration pcmWorkflowConfiguration = (IPCMWorkflowConfiguration) config;
        QualityObjective qualityObjective = pcmWorkflowConfiguration.getQualityObjective();
        if (qualityObjective != QualityObjective.RELIABILITY) {
            return 0;
        }

        return 1;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config, String launcherName,
            LaunchDescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        IModelledPcmWorkflowConfiguration workflowConfiguration = (IModelledPcmWorkflowConfiguration) config;
        ModelledModelLoader.Factory modelledModelLoaderFactory = (ModelledModelLoader.Factory) modelLoaderFactory;
        ModelledReliabilityPcmExperienceSimulationExecutorFactory factory = new ModelledReliabilityPcmExperienceSimulationExecutorFactory(
                workflowConfiguration, modelledModelLoaderFactory,
                new SimulatedExperienceStore<>(accessor, descriptionProvider), seedProvider, accessor, resourcePath);
        return factory.create();
    }

}
