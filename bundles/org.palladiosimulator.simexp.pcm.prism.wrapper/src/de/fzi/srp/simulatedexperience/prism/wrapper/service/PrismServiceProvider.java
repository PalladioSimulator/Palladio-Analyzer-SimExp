package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class PrismServiceProvider implements ServiceProvider {

    @Override
    public void register(ServiceRegistration serviceRegistration) {
        ILog logger = Platform.getLog(getClass());

        String prism = System.getenv("PRISM");
        if (prism == null) {
            // In the launcher under Environment add environment variable:
            // WINDOWS: 'PATH' with '${env_var:PRISM}\lib'
            // LINUX: 'LD_LIBRARY_PATH' with '${env_var:PRISM}/lib'
            // See https://github.com/prismmodelchecker/prism/wiki/Setting-up-Eclipse
            logger.error("environment variable 'PRISM' not found -> unable to register PrismService");
        } else {
            serviceRegistration.registerAService(ServiceEntry.service(PrismService.class)
                .isProvidedBy(PrismInvocationService.class));
        }
    }
}
