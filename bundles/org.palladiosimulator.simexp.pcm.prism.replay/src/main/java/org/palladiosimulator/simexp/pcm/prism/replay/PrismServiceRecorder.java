package org.palladiosimulator.simexp.pcm.prism.replay;

import java.nio.file.Path;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

public class PrismServiceRecorder implements PrismService {
    private final PrismService delegate;

    public PrismServiceRecorder(PrismService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void initialise(Path logFilePath, String strategyId) {
        delegate.initialise(logFilePath, strategyId);
    }

    @Override
    public PrismResult modelCheck(PrismContext context) {
        PrismResult modelCheckResult = delegate.modelCheck(context);
        return modelCheckResult;
    }

}
