package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class EAOptimizerLaunchFactory implements ILaunchFactory {

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.MODELLED) {
            return 0;
        }
        IModelledWorkflowConfiguration modelledWorkflowConfiguration = (IModelledWorkflowConfiguration) config;
        if (modelledWorkflowConfiguration.getOptimizationType() != ModelledOptimizationType.EVOLUTIONARY_ALGORITHM) {
            return 0;
        }

        // TODO:

        return 0;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory) {
        // TODO Auto-generated method stub
        return null;
    }

}
