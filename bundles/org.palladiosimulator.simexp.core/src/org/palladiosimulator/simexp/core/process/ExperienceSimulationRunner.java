package org.palladiosimulator.simexp.core.process;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface ExperienceSimulationRunner<S> {

    public void simulate(State<S> state);
}
