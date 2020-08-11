package org.palladiosimulator.simexp.core.process;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public interface ExperienceSimulationRunner {

	public default void initSimulationRun() {
		//This is only optional and implementation specific
	}
	
	public void simulate(SelfAdaptiveSystemState<?> sasState);
}
