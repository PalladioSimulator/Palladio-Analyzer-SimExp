package org.palladiosimulator.simexp.distribution.apache.factory;

import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class ApacheServiceProvider implements ServiceProvider {

    @Override
    public void register(ServiceRegistration serviceRegistration) {
        serviceRegistration.registerService(ServiceEntry.service(ProbabilityDistributionFactory.class)
            .isProvidedBy(ProbabilityDistributionFactoryAdapter.class));
    }
}
