package org.palladiosimulator.simexp.workflow.launcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.ISimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class SimulationExecutorLookup {
    private static final String LAUNCH_ID = "org.palladiosimulator.simexp.workflow.launch";

    public SimulationExecutor lookupSimulationExecutor(ISimExpWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider)
            throws CoreException {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        List<ILaunchFactory> factories = lookupFactories(registry);
        List<Pair<ILaunchFactory, Integer>> candidates = createCandidates(config, factories);
        ILaunchFactory launchFactory = selectCandidate(candidates);
        if (launchFactory != null) {
            PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
            return launchFactory.createSimulationExecutor(config, launchDescriptionProvider, seedProvider,
                    modelLoaderFactory);
        }

        return null;
    }

    private List<Pair<ILaunchFactory, Integer>> createCandidates(ISimExpWorkflowConfiguration config,
            List<ILaunchFactory> factories) {
        List<Pair<ILaunchFactory, Integer>> candidates = new ArrayList<>();
        for (ILaunchFactory factory : factories) {
            int value = factory.canHandle(config);
            if (value > 0) {
                candidates.add(new ImmutablePair<>(factory, value));
            }
        }
        return candidates;
    }

    private ILaunchFactory selectCandidate(List<Pair<ILaunchFactory, Integer>> candidates) {
        Pair<ILaunchFactory, Integer> maxPair = candidates.stream()
            .max(Comparator.comparing(p -> p.getRight()))
            .orElse(null);
        return maxPair.getKey();
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
