package org.palladiosimulator.simexp.pcm.modelled.prism;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledPrismSimulationExecutorLaunchFactory implements ILaunchFactory {

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.MODELLED) {
            return 0;
        }
        SimulationEngine simulationEngine = config.getSimulationEngine();
        if (simulationEngine != SimulationEngine.PRISM) {
            return 0;
        }

        return 1;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, SimulatedExperienceAccessor accessor) {
        IModelledPrismWorkflowConfiguration workflowConfiguration = (IModelledPrismWorkflowConfiguration) config;
        ModelledModelLoader.Factory modelledModelLoaderFactory = (ModelledModelLoader.Factory) modelLoaderFactory;
        ModelledPrismPcmExperienceSimulationExecutorFactory factory = new ModelledPrismPcmExperienceSimulationExecutorFactory(
                workflowConfiguration, modelledModelLoaderFactory,
                new SimulatedExperienceStore<>(accessor, descriptionProvider), seedProvider, accessor);
        return factory.create();
    }

}
