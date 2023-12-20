package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class MarkovianDecorator<S, A, R, D> implements Markovian<S, A, R, D> {

    protected Markovian<S, A, R, D> decoratedMarkovian = null;

    public MarkovianDecorator(Markovian<S, A, R, D> decoratedMarkovian) {
        this.decoratedMarkovian = decoratedMarkovian;
    }

    @Override
    public Sample<S, A, R> determineInitialState() {
        return decoratedMarkovian.determineInitialState();
    }

    @Override
    public ProbabilityMassFunction<D> getInitialStateDistribution() {
        return decoratedMarkovian.getInitialStateDistribution();
    }
}
