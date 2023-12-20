package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface Markovian<S, A, R, D> {

    public void drawSample(Sample<S, A, R> sample);

    public Sample<S, A, R> determineInitialState();

    public ProbabilityMassFunction<D> getInitialStateDistribution();

}
