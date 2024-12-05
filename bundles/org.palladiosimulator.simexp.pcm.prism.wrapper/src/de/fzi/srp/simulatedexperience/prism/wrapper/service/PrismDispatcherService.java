package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.nio.file.Path;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.replay.PrismServiceRecorder;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

public class PrismDispatcherService implements PrismService {
    private final PrismService delegate;

    public PrismDispatcherService() {
        this.delegate = new PrismServiceRecorder(new PrismInvocationService());
    }

    @Override
    public void initialise(Path prismFolder, String strategyId) {
        delegate.initialise(prismFolder, strategyId);
    }

    @Override
    public PrismResult modelCheck(PrismContext context) {
        return delegate.modelCheck(context);
    }

}
