package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTBaseReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.IDeltaIoToReconfCustomizerResolver;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons;

public class DeltaIoToReconfCustomizerResolver implements IDeltaIoToReconfCustomizerResolver {

    public DeltaIoToReconfCustomizerResolver() {
    }

    @Override
    public IDeltaIoToReconfiguration resolveDeltaIoTReconfCustomizer(SharedKnowledge knowledge) {
        Set<QVToReconfiguration> qvtoReconfigurations = findOptions(knowledge);
        return retrieveReconfiguration(DeltaIoTNetworkReconfiguration.class, qvtoReconfigurations)
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
    }

    private Set<QVToReconfiguration> findOptions(SharedKnowledge knowledge) {
        Set<?> options = (Set<?>) knowledge.getValue(DeltaIoTCommons.OPTIONS_KEY)
            .orElseThrow();
        return options.stream()
            .filter(QVToReconfiguration.class::isInstance)
            .map(QVToReconfiguration.class::cast)
            .collect(Collectors.toSet());
    }

    @Override
    public IDeltaIoToReconfiguration resolveDeltaIoTReconfCustomizer(Set<QVToReconfiguration> options) {
        DeltaIoTBaseReconfiguration customizer = retrieveReconfiguration(DeltaIoTBaseReconfiguration.class, options)
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
        return customizer;
    }

    private <T extends QVToReconfiguration> Optional<T> retrieveReconfiguration(Class<T> reconfClass,
            Set<QVToReconfiguration> options) {
        Stream<T> map = options.stream()
            .filter(reconfClass::isInstance)
            .map(reconfClass::cast);
        return map.findFirst();
    }
}
