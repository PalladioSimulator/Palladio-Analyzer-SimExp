package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public interface IDeltaIoToReconfCustomizerFactory {

    IDeltaIoToReconfiguration create(QVToReconfiguration qvto);
}
