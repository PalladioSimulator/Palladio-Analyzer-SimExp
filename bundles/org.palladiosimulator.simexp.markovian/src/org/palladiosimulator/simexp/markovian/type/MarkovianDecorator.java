package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class MarkovianDecorator<A, R> implements Markovian<A, R> {

    protected Markovian<A, R> decoratedMarkovian = null;

    public MarkovianDecorator(Markovian<A, R> decoratedMarkovian) {
        this.decoratedMarkovian = decoratedMarkovian;
    }

    @Override
    public Sample<A, R> determineInitialState() {
        return decoratedMarkovian.determineInitialState();
    }

    @Override
    public ProbabilityMassFunction<State> getInitialStateDistribution() {
        return decoratedMarkovian.getInitialStateDistribution();
    }
}
