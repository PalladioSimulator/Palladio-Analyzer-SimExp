package org.palladiosimulator.simexp.pcm.perceiption;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

//TODO refactor to EnvironmentToPcmElementBinder
public class PcmEnvironmentalState extends EnvironmentalState implements PcmModelChange {

	private final List<PcmModelChange> decoratedModelChanges = new ArrayList<PcmModelChange>();
	
//	public PcmEnvironmentalState(PcmModelChange decoratedModelChange, PerceivedValue<?> value) {
//		super(value, false, false);
//		this.decoratedModelChanges.add(decoratedModelChange);
//	}

	public PcmEnvironmentalState(List<PcmModelChange> decoratedModelChange, PerceivedValue<?> value) {
	    super(value, false, false);
	    this.decoratedModelChanges.addAll(decoratedModelChange);
	}
	
	@Override
	public void apply(PerceivedValue<?> change) {
	    for (PcmModelChange decoratedModelChange : decoratedModelChanges) {
	        decoratedModelChange.apply(change);
        }
	}
	
}
