package org.palladiosimulator.simexp.pcm.modelled.prism;

import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoToReconfLocalQualityCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;

public class PrismQVToReconfigurationProvider implements IQVToReconfigurationProvider {
    private final IQVToReconfigurationProvider qvtoReconfigurationProvider;
    private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;

    public PrismQVToReconfigurationProvider(IQVToReconfigurationProvider qvtoReconfigurationProvider,
            DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
        this.qvtoReconfigurationProvider = qvtoReconfigurationProvider;
        this.reconfParamsRepo = reconfParamsRepo;
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurations() {
        Set<QVToReconfiguration> reconfigurations = new HashSet<>();

        Set<QVToReconfiguration> reconfigurationList = qvtoReconfigurationProvider.getReconfigurations();
        IDeltaIoToReconfCustomizerFactory customizerFactory = new DeltaIoToReconfLocalQualityCustomizerFactory(
                reconfParamsRepo);
        reconfigurationList.stream()
            .forEach(qvto -> {
                SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) qvto;
                IDeltaIoToReconfiguration customizer = customizerFactory.create(singleQVToReconfiguration);
                reconfigurations.add(customizer);
            });
        return reconfigurations;
    }

}
