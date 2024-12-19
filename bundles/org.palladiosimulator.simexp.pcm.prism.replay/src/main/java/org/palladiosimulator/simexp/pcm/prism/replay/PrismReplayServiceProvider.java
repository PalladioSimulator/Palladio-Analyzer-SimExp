package org.palladiosimulator.simexp.pcm.prism.replay;

import org.palladiosimulator.simexp.pcm.prism.service.PrismRecorderService;
import org.palladiosimulator.simexp.pcm.prism.service.PrismReplayService;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistration;

public class PrismReplayServiceProvider implements ServiceProvider {
    @Override
    public void register(ServiceRegistration serviceRegistration) {
        serviceRegistration.registerService(ServiceEntry.service(PrismRecorderService.class)
            .isProvidedBy(PrismServiceRecorder.class));
        serviceRegistration.registerService(ServiceEntry.service(PrismReplayService.class)
            .isProvidedBy(PrismServiceReplay.class));
    }
}
