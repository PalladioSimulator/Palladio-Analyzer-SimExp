package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;

public interface IDeltaIoToReconfCustomizerResolver {
    IDeltaIoToReconfiguration resolveDeltaIoTReconfCustomizer(SharedKnowledge knowledge);
}
