package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;

public class DeltaIoToReconfNetworkCustomizerFactory implements IDeltaIoToReconfCustomizerFactory {

    @Override
    public IDeltaIoToReconfiguration create(QVToReconfiguration qvto,
            DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
        if (DeltaIoTNetworkReconfiguration.isCorrectQvtReconfguration(qvto)) {
            DeltaIoTBaseReconfiguration deltaIoTBaseReconfiguration = (DeltaIoTBaseReconfiguration) qvto;
            return new DeltaIoTNetworkReconfiguration(deltaIoTBaseReconfiguration, reconfParamsRepo);
        }
        return null;
    }

}
