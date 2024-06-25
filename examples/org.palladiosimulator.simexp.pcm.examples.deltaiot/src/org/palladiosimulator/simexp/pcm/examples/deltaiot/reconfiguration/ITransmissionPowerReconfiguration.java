package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.Map;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

import de.uka.ipd.sdq.stoex.VariableReference;

public interface ITransmissionPowerReconfiguration extends QVToReconfiguration {
    void adjustTransmissionPower(Map<VariableReference, Integer> powerSetting);
}
