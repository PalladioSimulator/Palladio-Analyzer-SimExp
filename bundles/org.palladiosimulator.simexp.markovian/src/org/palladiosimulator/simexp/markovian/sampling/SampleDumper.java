package org.palladiosimulator.simexp.markovian.sampling;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface SampleDumper {

    void dump(State source);
}
