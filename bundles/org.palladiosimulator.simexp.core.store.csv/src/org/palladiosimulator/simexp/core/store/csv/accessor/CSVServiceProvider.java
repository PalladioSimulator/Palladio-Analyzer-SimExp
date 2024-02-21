package org.palladiosimulator.simexp.core.store.csv.accessor;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class CSVServiceProvider implements ServiceProvider {

    @Override
    public void register(ServiceRegistration serviceRegistration) {
        serviceRegistration.registerService(ServiceEntry.service(SimulatedExperienceAccessor.class)
            .isProvidedBy(CsvAccessor.class));
    }
}
