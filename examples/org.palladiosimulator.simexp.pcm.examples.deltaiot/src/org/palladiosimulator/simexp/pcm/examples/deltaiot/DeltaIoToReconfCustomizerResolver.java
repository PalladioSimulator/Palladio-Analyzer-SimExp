package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.IDeltaIoToReconfCustomizerResolver;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons;

public class DeltaIoToReconfCustomizerResolver implements IDeltaIoToReconfCustomizerResolver {

    public DeltaIoToReconfCustomizerResolver() {
    }

    @Override
    public IDeltaIoToReconfiguration resolveDeltaIoTReconfCustomizer(SharedKnowledge knowledge) {
        return retrieveReconfiguration(DeltaIoTNetworkReconfiguration.class, DeltaIoTCommons.findOptions(knowledge))
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
    }

    private <T extends QVToReconfiguration> Optional<T> retrieveReconfiguration(Class<T> reconfClass,
            Set<QVToReconfiguration> options) {
        Stream<T> map = options.stream()
            .filter(reconfClass::isInstance)
            .map(reconfClass::cast);
        return map.findFirst();
    }
}
