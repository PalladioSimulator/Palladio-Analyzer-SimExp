package org.palladiosimulator.simexp.dsl.ea.api.dispatcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class DispatcherLookup {
    private static final String ID = "org.palladiosimulator.simexp.dsl.ea.api.dispatcher";

    private final IExtensionRegistry registry;

    public DispatcherLookup() {
        this(Platform.getExtensionRegistry());
    }

    DispatcherLookup(IExtensionRegistry registry) {
        this.registry = registry;
    }

    public List<String> lookupProviderNames() throws CoreException {
        List<IDispatcherProvider> provider = lookupProvider(registry);
        List<String> names = provider.stream()
            .map(p -> p.getName())
            .toList();
        return names;
    }

    public IDisposeableEAFitnessEvaluator createEvaluator(String dispatcherName, IWorkflowConfiguration config,
            String launcherName, LaunchDescriptionProvider launchDescriptionProvider,
            Optional<ISeedProvider> seedProvider, Factory modelLoaderFactory, Path resourcePath) throws CoreException {
        List<IDispatcherProvider> providers = lookupProvider(registry);
        for (IDispatcherProvider provider : providers) {
            if (dispatcherName.equals(provider.getName())) {
                IDisposeableEAFitnessEvaluator evaluator = provider.createEvaluator(config, launcherName,
                        launchDescriptionProvider, seedProvider, modelLoaderFactory, resourcePath);
                return evaluator;
            }
        }
        throw new RuntimeException("unknown dispatcher: " + dispatcherName);
    }

    private List<IDispatcherProvider> lookupProvider(IExtensionRegistry registry) throws CoreException {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(ID);
        List<IDispatcherProvider> factories = new ArrayList<>();
        for (IConfigurationElement e : config) {
            final Object o = e.createExecutableExtension("class");
            if (o instanceof IDispatcherProvider launchFactory) {
                factories.add(launchFactory);
            }
        }
        return factories;
    }
}
