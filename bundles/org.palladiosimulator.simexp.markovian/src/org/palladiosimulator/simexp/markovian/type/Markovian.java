package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface Markovian<S, A, R> {

    public void drawSample(Sample<S, A, R, State<S>> sample);

    public Sample<S, A, R, State<S>> determineInitialState();

    public ProbabilityMassFunction<State<S>> getInitialStateDistribution();

}
