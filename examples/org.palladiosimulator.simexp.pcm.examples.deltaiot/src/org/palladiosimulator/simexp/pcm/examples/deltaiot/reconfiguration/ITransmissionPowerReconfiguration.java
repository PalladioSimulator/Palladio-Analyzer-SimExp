package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.Map;

import de.uka.ipd.sdq.stoex.VariableReference;

public interface ITransmissionPowerReconfiguration extends IDeltaIoToReconfiguration {
    void adjustTransmissionPower(Map<VariableReference, Integer> powerSetting);

    boolean canBeAdjusted(Map<VariableReference, Integer> powerValues);
}
