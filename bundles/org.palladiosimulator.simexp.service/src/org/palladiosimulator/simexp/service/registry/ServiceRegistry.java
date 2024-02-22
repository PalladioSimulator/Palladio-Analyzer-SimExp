package org.palladiosimulator.simexp.service.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public enum ServiceRegistry implements ServiceDiscovery, ServiceRegistration {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(ServiceRegistry.class.getName());
    private static final String SERVICE_REGISTRY_ID = "org.palladiosimulator.simexp.service.serviceregistry";

    @Override
    public void setUp(List<ServiceEntry<?>> services) {
    }

    @Override
    public <T> Optional<T> findService(Class<T> requiredClass) {
        for (ServiceEntry<?> each : register) {
            if (each.getRequiredClass()
                .equals(requiredClass)) {
                return Optional.ofNullable(toServiceImpl(each));
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> List<T> findServices(Class<T> requiredClass) {
        List<T> results = new ArrayList<>();
        for (ServiceEntry<?> each : register) {
            if (each.getRequiredClass()
                .equals(requiredClass)) {
                results.add(toServiceImpl(each));
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private <T> T toServiceImpl(ServiceEntry<?> serviceEntry) {
        T result = null;
        try {
            result = (T) serviceEntry.getProvidedClass()
                .getDeclaredConstructor()
                .newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            LOGGER.error(String.format("Failed to provide service implementation for service '%s'",
                    serviceEntry.getProvidedClass()
                        .getName()),
                    e);
        }
        return result;
    }

    private final List<ServiceEntry<?>> register = new ArrayList<>();

    private ServiceRegistry() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        registerProvider(registry);
    }

    public static ServiceDiscovery get() {
        return ServiceRegistry.INSTANCE;
    }

    private void registerProvider(IExtensionRegistry registry) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(SERVICE_REGISTRY_ID);
        try {
            for (IConfigurationElement e : config) {
                Object providerClass = e.createExecutableExtension("class");
                if (providerClass instanceof ServiceProvider) {
                    ServiceProvider serviceProvider = (ServiceProvider) providerClass;
                    serviceProvider.register(this);
                }
            }
        } catch (CoreException e) {
            throw new RuntimeException("registration failure", e);
        }
    }

    @Override
    public void registerService(ServiceEntry<?> serviceToRegister) {
        register.add(serviceToRegister);
    }
}
