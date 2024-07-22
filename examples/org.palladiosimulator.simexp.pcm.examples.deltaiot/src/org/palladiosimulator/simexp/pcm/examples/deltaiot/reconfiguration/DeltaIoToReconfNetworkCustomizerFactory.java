package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;

public class DeltaIoToReconfNetworkCustomizerFactory implements IDeltaIoToReconfCustomizerFactory {
    private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;

    public DeltaIoToReconfNetworkCustomizerFactory(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
        this.reconfParamsRepo = reconfParamsRepo;
    }

    @Override
    public IDeltaIoToReconfiguration create(QVToReconfiguration qvto) {
        if (DeltaIoTNetworkReconfiguration.isCorrectQvtReconfguration(qvto)) {
            DeltaIoTBaseReconfiguration deltaIoTBaseReconfiguration = (DeltaIoTBaseReconfiguration) qvto;
            return new DeltaIoTNetworkReconfiguration(deltaIoTBaseReconfiguration, reconfParamsRepo);
        }
        return null;
    }

}
