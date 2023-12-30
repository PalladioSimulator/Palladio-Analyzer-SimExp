package org.palladiosimulator.simexp.core.process;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public interface ExperienceSimulationRunner<S, A> {

    public void simulate(SelfAdaptiveSystemState<S, A> sasState);
}
