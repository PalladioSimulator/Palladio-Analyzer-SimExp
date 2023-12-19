package org.palladiosimulator.simexp.markovian.type;

import static org.palladiosimulator.simexp.markovian.access.SampleModelAccessor.createInitialSample;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator.NavigationContext;

public class BasicMarkovian<T> implements Markovian<T> {

    private final ProbabilityMassFunction<T> initialStateDistribution;
    private final StateSpaceNavigator<T> stateSpaceNavigator;

    public BasicMarkovian(ProbabilityMassFunction<T> initialStateDistribution,
            StateSpaceNavigator<T> stateSpaceNavigator) {
        this.initialStateDistribution = initialStateDistribution;
        this.stateSpaceNavigator = stateSpaceNavigator;
    }

    @Override
    public void drawSample(Sample<T> sample) {
        NavigationContext<T> context = NavigationContext.of(sample);
        sample.setNext(stateSpaceNavigator.navigate(context));
    }

    @Override
    public Sample<T> determineInitialState() {
        org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample<T> sample = initialStateDistribution
            .drawSample();
        State<T> value = (State<T>) sample.getValue();
        return createInitialSample(value);
    }

    @Override
    public ProbabilityMassFunction<T> getInitialStateDistribution() {
        return initialStateDistribution;
    }

}
