package org.palladiosimulator.simexp.workflow.api;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface ILaunchFactory {
    int canHandle(IWorkflowConfiguration config);

    SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config, String launcherName,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory, SimulatedExperienceAccessor accessor, Path resourcePath)
            throws CoreException;
}
