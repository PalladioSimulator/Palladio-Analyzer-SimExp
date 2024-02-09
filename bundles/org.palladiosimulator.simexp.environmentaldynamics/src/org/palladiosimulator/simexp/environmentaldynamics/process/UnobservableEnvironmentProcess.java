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

public class UnobservableEnvironmentProcess<A, Aa extends Action<A>, R, V> extends EnvironmentProcess<A, R, V> {

    public UnobservableEnvironmentProcess(MarkovModel<A, R> model, ProbabilityMassFunction<State> initialDistribution,
            ObservationProducer obsProducer) {
        super(buildMarkovian(buildEnvironmentalDynamics(model), initialDistribution, obsProducer), model,
                initialDistribution);
    }

    public UnobservableEnvironmentProcess(DerivableEnvironmentalDynamic<A> dynamics,
            ProbabilityMassFunction<State> initialDistribution, ObservationProducer obsProducer) {
        super(buildMarkovian(dynamics, initialDistribution, obsProducer), dynamics, initialDistribution);
    }

    private static <A, Aa extends Action<A>, R> Markovian<A, R> buildMarkovian(
            StateSpaceNavigator<A> environmentalDynamics, ProbabilityMassFunction<State> initialDistribution,
            ObservationProducer obsProducer) {
        MarkovianBuilder<A, Aa, R>.HMMBuilder builder = MarkovianBuilder.<A, Aa, R> createHiddenMarkovModel();
        builder = builder.createStateSpaceNavigator(environmentalDynamics);
        builder = builder.withInitialStateDistribution(initialDistribution);
        builder = builder.handleObservationsWith(obsProducer);
        return builder.build();
    }

    @Override
    public PerceivableEnvironmentalState<V> determineNextGiven(PerceivableEnvironmentalState<V> last) {
        EnvironmentalStateObservation<V> observation = EnvironmentalStateObservation.class.cast(last);
        EnvironmentalState<V> hiddenState = observation.getHiddenState();
        return (EnvironmentalStateObservation<V>) determineNextSampleGiven(hiddenState).getObservation();
    }

    @Override
    public PerceivableEnvironmentalState<V> determineInitial() {
        // TODO Could be better solved... see HiddenMarkovian
        Sample<A, R> initialSample = sampler.drawInitialSample();
        /*
         * State<S> currentState = initialSample.getCurrent(); return
         * (PerceivableEnvironmentalState) currentState.getProduces() .get(0);
         */
        Observation observation = initialSample.getObservation();
        return (PerceivableEnvironmentalState<V>) observation;
    }

}
