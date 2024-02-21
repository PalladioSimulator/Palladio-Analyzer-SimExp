package org.palladiosimulator.simexp.service.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

public class ServiceRegistry implements ServiceDiscovery, ServiceRegistration {

    private static final Logger LOGGER = Logger.getLogger(ServiceRegistry.class.getName());

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

    private static ServiceRegistry serviceRegistry = null;

    private ServiceRegistry() {
    }

    private static ServiceRegistry getServiceRegistry() {
        if (serviceRegistry == null) {
            // discoveryInstance = new GuiceServiceDiscovery();
            serviceRegistry = new ServiceRegistry();
        }
        return serviceRegistry;
    }

    public static ServiceDiscovery get() {
        return getServiceRegistry();
    }

    @Override
    public void registerAService(ServiceEntry<?> serviceToRegister) {
        register.add(serviceToRegister);
    }

    public static void registerService(ServiceEntry<?> serviceToRegister) {
        ServiceRegistration serviceRegistration = getServiceRegistry();
        serviceRegistration.registerAService(serviceToRegister);
    }
}
