package org.palladiosimulator.simexp.core.action;

import org.palladiosimulator.simexp.core.entity.StringRepresentable;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl;

public abstract class Reconfiguration<T> extends ActionImpl<T> implements StringRepresentable {
	
	@Override
	public String toString() {
		return getStringRepresentation();
	}

}
