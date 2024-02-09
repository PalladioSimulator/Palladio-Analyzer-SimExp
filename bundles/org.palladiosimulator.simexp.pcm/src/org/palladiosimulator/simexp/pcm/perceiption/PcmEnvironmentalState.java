package org.palladiosimulator.simexp.pcm.perceiption;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

//TODO refactor to EnvironmentToPcmElementBinder
public class PcmEnvironmentalState<V> extends EnvironmentalState<V> implements PcmModelChange<V> {

    private final List<PcmModelChange<V>> decoratedModelChanges = new ArrayList<>();

    public PcmEnvironmentalState(List<PcmModelChange<V>> decoratedModelChange, PerceivedValue<V> value) {
        super(value, false, false);
        this.decoratedModelChanges.addAll(decoratedModelChange);
    }

    @Override
    public void apply(PerceivedValue<V> change) {
        for (PcmModelChange<V> decoratedModelChange : decoratedModelChanges) {
            decoratedModelChange.apply(change);
        }
    }

}
