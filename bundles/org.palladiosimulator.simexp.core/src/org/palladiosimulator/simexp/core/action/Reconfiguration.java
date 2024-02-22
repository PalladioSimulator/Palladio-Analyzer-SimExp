package org.palladiosimulator.simexp.core.action;

import org.palladiosimulator.simexp.core.entity.StringRepresentable;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl;

public abstract class Reconfiguration<A> extends ActionImpl<A> implements StringRepresentable {
	
	@Override
	public String toString() {
		return getStringRepresentation();
	}

}
