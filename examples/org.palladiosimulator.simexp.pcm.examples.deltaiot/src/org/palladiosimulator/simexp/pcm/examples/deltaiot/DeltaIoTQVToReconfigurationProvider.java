package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.LinkedHashSet;
import java.util.Set;

import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;

public class DeltaIoTQVToReconfigurationProvider implements IQVToReconfigurationProvider {
    private final IQVToReconfigurationProvider qvtoReconfigurationProvider;
    private final IDeltaIoToReconfCustomizerFactory reconfCustomizerFactory;

    public DeltaIoTQVToReconfigurationProvider(IQVToReconfigurationProvider qvtoReconfigurationProvider,
            IDeltaIoToReconfCustomizerFactory reconfCustomizerFactory) {
        this.qvtoReconfigurationProvider = qvtoReconfigurationProvider;
        this.reconfCustomizerFactory = reconfCustomizerFactory;
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurations() {
        Set<QVToReconfiguration> reconfigurations = new LinkedHashSet<>();
        Set<QVToReconfiguration> qvtoReconfigurations = qvtoReconfigurationProvider.getReconfigurations();
        for (QVToReconfiguration qvto : qvtoReconfigurations) {
            IDeltaIoToReconfiguration customizer = reconfCustomizerFactory.create(qvto);
            if (customizer != null) {
                reconfigurations.add(customizer);
            }
        }
        return reconfigurations;
    }
}
