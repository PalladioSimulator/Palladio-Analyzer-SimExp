package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class MarkovianDecorator<S, A, R, O> implements Markovian<S, A, R, O> {

    protected Markovian<S, A, R, O> decoratedMarkovian = null;

    public MarkovianDecorator(Markovian<S, A, R, O> decoratedMarkovian) {
        this.decoratedMarkovian = decoratedMarkovian;
    }

    @Override
    public Sample<S, A, R, O> determineInitialState() {
        return decoratedMarkovian.determineInitialState();
    }

    @Override
    public ProbabilityMassFunction<State<S>> getInitialStateDistribution() {
        return decoratedMarkovian.getInitialStateDistribution();
    }
}
