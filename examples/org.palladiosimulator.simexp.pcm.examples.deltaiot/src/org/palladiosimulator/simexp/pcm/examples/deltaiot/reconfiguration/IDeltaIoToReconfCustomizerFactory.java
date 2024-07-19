package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;

public interface IDeltaIoToReconfCustomizerFactory {

    IDeltaIoToReconfiguration create(QVToReconfiguration qvto, DeltaIoTReconfigurationParamRepository reconfParamsRepo);
}
