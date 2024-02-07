package org.palladiosimulator.simexp.environmentaldynamics.process;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class UnobservableEnvironmentProcess<S, A, Aa extends Action<A>, R, V> extends EnvironmentProcess<S, A, R> {

    public UnobservableEnvironmentProcess(MarkovModel<S, A, R> model,
            ProbabilityMassFunction<State<S>> initialDistribution, ObservationProducer<S> obsProducer) {
        super(buildMarkovian(buildEnvironmentalDynamics(model), initialDistribution, obsProducer), model,
                initialDistribution);
    }

    public UnobservableEnvironmentProcess(DerivableEnvironmentalDynamic<S, A> dynamics,
            ProbabilityMassFunction<State<S>> initialDistribution, ObservationProducer<S> obsProducer) {
        super(buildMarkovian(dynamics, initialDistribution, obsProducer), dynamics, initialDistribution);
    }

    private static <S, A, Aa extends Action<A>, R> Markovian<S, A, R> buildMarkovian(
            StateSpaceNavigator<S, A> environmentalDynamics, ProbabilityMassFunction<State<S>> initialDistribution,
            ObservationProducer<S> obsProducer) {
        MarkovianBuilder<S, A, Aa, R>.HMMBuilder builder = MarkovianBuilder.<S, A, Aa, R> createHiddenMarkovModel();
        builder = builder.createStateSpaceNavigator(environmentalDynamics);
        builder = builder.withInitialStateDistribution(initialDistribution);
        builder = builder.handleObservationsWith(obsProducer);
        return builder.build();
    }

    @Override
    public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
        EnvironmentalState<S> hiddenState = EnvironmentalStateObservation.class.cast(last)
            .getHiddenState();
        Observation<S> observation = determineNextSampleGiven(hiddenState).getObservation();
        return (PerceivableEnvironmentalState) observation;
    }

    @Override
    public PerceivableEnvironmentalState determineInitial() {
        // TODO Could be better solved... see HiddenMarkovian
        Sample<S, A, R, S> initialSample = sampler.drawInitialSample();
        /*
         * State<S> currentState = initialSample.getCurrent(); return
         * (PerceivableEnvironmentalState) currentState.getProduces() .get(0);
         */
        Observation<S> observation = initialSample.getObservation();
        return (PerceivableEnvironmentalState) observation;
    }

}
