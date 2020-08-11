package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class DerivableEnvironmentalDynamic extends InductiveStateSpaceNavigator implements EnvironmentalDynamic {

	protected boolean isHiddenProcess;
	
	@Override
	public boolean isHiddenProcess() {
		return isHiddenProcess;
	}
	
}
