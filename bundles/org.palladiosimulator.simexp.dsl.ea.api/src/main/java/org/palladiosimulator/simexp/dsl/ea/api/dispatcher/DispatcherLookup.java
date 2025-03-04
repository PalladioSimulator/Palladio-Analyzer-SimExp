package org.palladiosimulator.simexp.dsl.ea.api.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

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
