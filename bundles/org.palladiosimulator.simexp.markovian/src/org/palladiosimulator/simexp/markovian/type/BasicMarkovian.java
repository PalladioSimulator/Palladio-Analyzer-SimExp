package org.palladiosimulator.simexp.markovian.type;

import static org.palladiosimulator.simexp.markovian.access.SampleModelAccessor.createInitialSample;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator.NavigationContext;

public class BasicMarkovian<A, R> implements Markovian<A, R> {

    private final ProbabilityMassFunction<State> initialStateDistribution;
    private final StateSpaceNavigator<A> stateSpaceNavigator;

    public BasicMarkovian(ProbabilityMassFunction<State> initialStateDistribution,
            StateSpaceNavigator<A> stateSpaceNavigator) {
        this.initialStateDistribution = initialStateDistribution;
        this.stateSpaceNavigator = stateSpaceNavigator;
    }

    @Override
    public void drawSample(Sample<A, R> sample) {
        NavigationContext<A> context = NavigationContext.of(sample);
        sample.setNext(stateSpaceNavigator.navigate(context));
    }

    @Override
    public Sample<A, R> determineInitialState() {
        org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample<State> sample = initialStateDistribution
            .drawSample();
        State value = sample.getValue();
        return createInitialSample(value);
    }

    @Override
    public ProbabilityMassFunction<State> getInitialStateDistribution() {
        return initialStateDistribution;
    }

}
