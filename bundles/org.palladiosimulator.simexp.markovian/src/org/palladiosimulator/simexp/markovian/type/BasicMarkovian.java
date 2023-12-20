package org.palladiosimulator.simexp.markovian.type;

import static org.palladiosimulator.simexp.markovian.access.SampleModelAccessor.createInitialSample;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator.NavigationContext;

public class BasicMarkovian<S, A, R> implements Markovian<S, A, R, State<S>> {

    private final ProbabilityMassFunction<State<S>> initialStateDistribution;
    private final StateSpaceNavigator<S, A> stateSpaceNavigator;

    public BasicMarkovian(ProbabilityMassFunction<State<S>> initialStateDistribution,
            StateSpaceNavigator<S, A> stateSpaceNavigator) {
        this.initialStateDistribution = initialStateDistribution;
        this.stateSpaceNavigator = stateSpaceNavigator;
    }

    @Override
    public void drawSample(Sample<S, A, R> sample) {
        NavigationContext<S, A> context = NavigationContext.of(sample);
        sample.setNext(stateSpaceNavigator.navigate(context));
    }

    @Override
    public Sample<S, A, R> determineInitialState() {
        org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample<State<S>> sample = initialStateDistribution
            .drawSample();
        State<S> value = sample.getValue();
        return createInitialSample(value);
    }

    @Override
    public ProbabilityMassFunction<State<S>> getInitialStateDistribution() {
        return initialStateDistribution;
    }

}
