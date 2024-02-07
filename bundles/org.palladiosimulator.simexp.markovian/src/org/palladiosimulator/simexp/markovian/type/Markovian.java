package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface Markovian<S, A, R, O> {

    public void drawSample(Sample<S, A, R, O> sample);

    public Sample<S, A, R, O> determineInitialState();

    public ProbabilityMassFunction<State<S>> getInitialStateDistribution();

}
