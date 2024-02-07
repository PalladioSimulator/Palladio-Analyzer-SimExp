package org.palladiosimulator.simexp.core.process;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public interface ExperienceSimulationRunner<C, A> {

    public void simulate(SelfAdaptiveSystemState<C, A> sasState);
}
