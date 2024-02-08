package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface Markovian<A, R> {

    public void drawSample(Sample<A, R> sample);

    public Sample<A, R> determineInitialState();

    public ProbabilityMassFunction<State> getInitialStateDistribution();

}
