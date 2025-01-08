package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class PrismServiceProvider implements ServiceProvider {
    @Override
    public void register(ServiceRegistration serviceRegistration) {
        serviceRegistration.registerService(ServiceEntry.service(PrismService.class)
            .isProvidedBy(PrismInvocationService.class));
    }
}
