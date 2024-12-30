package org.palladiosimulator.simexp.workflow.launcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class SimulationExecutorLookup {
    private static final String LAUNCH_ID = "org.palladiosimulator.simexp.workflow.launch";

    public SimulationExecutor lookupSimulationExecutor(SimExpWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider)
            throws CoreException {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        List<ILaunchFactory> factories = lookupFactories(registry);
        for (ILaunchFactory factory : factories) {
            if (factory.canHandle(config)) {
                PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
                return factory.createSimulationExecutor(config, launchDescriptionProvider, seedProvider,
                        modelLoaderFactory);
            }
        }
        return null;
    }

    private List<ILaunchFactory> lookupFactories(IExtensionRegistry registry) throws CoreException {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(LAUNCH_ID);
        List<ILaunchFactory> factories = new ArrayList<>();
        for (IConfigurationElement e : config) {
            final Object o = e.createExecutableExtension("class");
            if (o instanceof ILaunchFactory launchFactory) {
                factories.add(launchFactory);
            }
        }
        return factories;
    }
}
