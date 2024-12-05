package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class PrismServiceProvider implements ServiceProvider {
    private static final Logger LOGGER = Logger.getLogger(PrismServiceProvider.class);

    @Override
    public void register(ServiceRegistration serviceRegistration) {
        String prism = System.getenv("PRISM");
        if (prism == null) {
            // In the launcher under Environment add environment variable:
            // WINDOWS: 'PATH' with '${env_var:PRISM}\lib'
            // LINUX: 'LD_LIBRARY_PATH' with '${env_var:PRISM}/lib'
            // See https://github.com/prismmodelchecker/prism/wiki/Setting-up-Eclipse
            LOGGER.error("environment variable 'PRISM' not found -> unable to register PrismService");
        } else {
            serviceRegistration.registerService(ServiceEntry.service(PrismService.class)
                .isProvidedBy(PrismDispatcherService.class));
        }
    }
}
