package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;

public class DeltaIoToReconfLocalQualityCustomizerFactory implements IDeltaIoToReconfCustomizerFactory {
    private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;

    public DeltaIoToReconfLocalQualityCustomizerFactory(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
        this.reconfParamsRepo = reconfParamsRepo;
    }

    @Override
    public IDeltaIoToReconfiguration create(QVToReconfiguration qvto) {
        if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(qvto)) {
            SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) qvto;
            return new DistributionFactorReconfiguration(singleQVToReconfiguration,
                    reconfParamsRepo.getDistributionFactors());
        }
        if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(qvto)) {
            SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) qvto;
            return new TransmissionPowerReconfiguration(singleQVToReconfiguration,
                    reconfParamsRepo.getTransmissionPower());
        }
        return null;
    }

}
