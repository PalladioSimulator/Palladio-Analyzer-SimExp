package org.palladiosimulator.simexp.core.process;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public interface ExperienceSimulationRunner {

	public void simulate(SelfAdaptiveSystemState<?> sasState);
}
