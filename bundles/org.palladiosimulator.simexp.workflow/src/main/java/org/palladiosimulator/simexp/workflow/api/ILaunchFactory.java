package org.palladiosimulator.simexp.workflow.api;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface ILaunchFactory {
    int canHandle(IWorkflowConfiguration config);

    SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory);
}
