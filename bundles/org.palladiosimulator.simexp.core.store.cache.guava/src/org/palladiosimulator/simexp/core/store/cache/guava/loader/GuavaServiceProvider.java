package org.palladiosimulator.simexp.core.store.cache.guava.loader;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceCache;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class GuavaServiceProvider implements ServiceProvider {

    @Override
    public void register(ServiceRegistration serviceRegistration) {
        serviceRegistration.registerService(ServiceEntry.service(SimulatedExperienceCache.class)
            .isProvidedBy(GuavaSimulatedExperienceCache.class));
    }
}
