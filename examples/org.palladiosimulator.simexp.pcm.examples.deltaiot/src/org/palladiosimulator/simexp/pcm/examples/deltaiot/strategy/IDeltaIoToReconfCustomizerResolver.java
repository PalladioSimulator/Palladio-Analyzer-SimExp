package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;

public interface IDeltaIoToReconfCustomizerResolver {
    IDeltaIoToReconfiguration resolveDeltaIoTReconfCustomizer(SharedKnowledge knowledge);

    IDeltaIoToReconfiguration retrieveDeltaIoTNetworkReconfiguration(Set<QVToReconfiguration> options);
}
