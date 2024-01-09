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
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class UnobservableEnvironmentProcess<S, A, Aa extends Action<A>, R> extends EnvironmentProcess<S, A, R> {

    private static class ObservationProducerProxy<S, A, R> implements ObservationProducer<S> {

        private ObservationProducer<S> representedProducer = null;

        public void represents(ObservationProducer<S> representedProducer) {
            this.representedProducer = representedProducer;
        }

        @Override
        public Observation<S> produceObservationGiven(State<S> emittingState) {
            return representedProducer.produceObservationGiven(emittingState);
        }

    }

    // TODO: singleton
    private final static ObservationProducerProxy PRODUCER_PROXY = new ObservationProducerProxy();

    public UnobservableEnvironmentProcess(MarkovModel<S, A, R> model,
            ProbabilityMassFunction<State<S>> initialDistribution, ObservationProducer<S> obsProducer) {
        super(model, initialDistribution);
        PRODUCER_PROXY.represents(obsProducer);
    }

    public UnobservableEnvironmentProcess(DerivableEnvironmentalDynamic<S, A> dynamics,
            ProbabilityMassFunction<State<S>> initialDistribution, ObservationProducer<S> obsProducer) {
        super(dynamics, initialDistribution);
        PRODUCER_PROXY.represents(obsProducer);
    }

    @Override
    protected Markovian<S, A, R> buildMarkovian(StateSpaceNavigator<S, A> environmentalDynamics,
            ProbabilityMassFunction<State<S>> initialDistribution) {
        MarkovianBuilder<S, A, Aa, R>.HMMBuilder builder = MarkovianBuilder.<S, A, Aa, R> createHiddenMarkovModel();
        builder = builder.createStateSpaceNavigator(environmentalDynamics);
        builder = builder.withInitialStateDistribution(initialDistribution);
        builder = builder.handleObservationsWith(PRODUCER_PROXY);
        return builder.build();
    }

    @Override
    public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
        EnvironmentalState<S> hiddenState = EnvironmentalStateObservation.class.cast(last)
            .getHiddenState();
        return (EnvironmentalStateObservation<S>) determineNextSampleGiven(hiddenState).getObservation();
    }

    @Override
    public PerceivableEnvironmentalState determineInitial() {
        // TODO Could be better solved... see HiddenMarkovian
        return (PerceivableEnvironmentalState) sampler.drawInitialSample()
            .getCurrent()
            .getProduces()
            .get(0);
    }

}
