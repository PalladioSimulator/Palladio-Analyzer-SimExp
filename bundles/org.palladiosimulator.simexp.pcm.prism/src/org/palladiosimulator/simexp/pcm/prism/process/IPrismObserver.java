package org.palladiosimulator.simexp.pcm.prism.process;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;

public interface IPrismObserver {
    void onContext(PrismContext context);
}
