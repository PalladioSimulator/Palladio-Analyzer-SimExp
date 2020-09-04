package org.palladiosimulator.simexp.environmentaldynamics.process;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class UnobservableEnvironmentProcess extends EnvironmentProcess {

	private static class ObservationProducerProxy implements ObservationProducer {

		private ObservationProducer representedProducer = null;

		public void represents(ObservationProducer representedProducer) {
			this.representedProducer = representedProducer;
		}

		@Override
		public Observation<?> produceObservationGiven(State emittingState) {
			return representedProducer.produceObservationGiven(emittingState);
		}

	}

	private final static ObservationProducerProxy PRODUCER_PROXY = new ObservationProducerProxy();

	public UnobservableEnvironmentProcess(MarkovModel model, ProbabilityMassFunction initialDistribution,
			ObservationProducer obsProducer) {
		super(model, initialDistribution);
		PRODUCER_PROXY.represents(obsProducer);
	}

	public UnobservableEnvironmentProcess(DerivableEnvironmentalDynamic dynamics,
			ProbabilityMassFunction initialDistribution, ObservationProducer obsProducer) {
		super(dynamics, initialDistribution);
		PRODUCER_PROXY.represents(obsProducer);
	}

	@Override
	protected Markovian buildMarkovian(StateSpaceNavigator environmentalDynamics,
			ProbabilityMassFunction initialDistribution) {
		return MarkovianBuilder.createHiddenMarkovModel().createStateSpaceNavigator(environmentalDynamics)
				.withInitialStateDistribution(initialDistribution).handleObservationsWith(PRODUCER_PROXY).build();
	}

	@Override
	public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
		EnvironmentalState hiddenState = EnvironmentalStateObservation.class.cast(last).getHiddenState();
		return (EnvironmentalStateObservation) determineNextSampleGiven(hiddenState).getObservation();
	}

	@Override
	public PerceivableEnvironmentalState determineInitial() {
		// TODO Could be better solved... see HiddenMarkovian
		return (PerceivableEnvironmentalState) sampler.drawInitialSample().getCurrent().getProduces().get(0);
	}

}
