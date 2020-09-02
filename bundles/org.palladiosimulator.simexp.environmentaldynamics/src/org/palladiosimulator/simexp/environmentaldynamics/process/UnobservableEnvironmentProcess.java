package org.palladiosimulator.simexp.environmentaldynamics.process;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.HiddenEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class UnobservableEnvironmentProcess extends EnvironmentProcess {

	private static class ObservationProducerDecorator implements ObservationProducer {
		
		private ObservationProducer obsProducer = null;
		
		public void setDecorated(ObservationProducer obsProducer) {
			this.obsProducer = obsProducer;
		}

		@Override
		public Observation<?> produceObservationGiven(State emittingState) {
			return obsProducer.produceObservationGiven(emittingState);
		}
		
	}
	
	private final static ObservationProducerDecorator OBS_DECORATOR = new ObservationProducerDecorator();

	public UnobservableEnvironmentProcess(MarkovModel model, ProbabilityMassFunction initialDistribution,
			ObservationProducer obsProducer) {
		super(model, initialDistribution);
		OBS_DECORATOR.setDecorated(obsProducer);
	}

	public UnobservableEnvironmentProcess(DerivableEnvironmentalDynamic dynamics,
			ProbabilityMassFunction initialDistribution, ObservationProducer obsProducer) {
		super(dynamics, initialDistribution);
		OBS_DECORATOR.setDecorated(obsProducer);
	}

	@Override
	protected Markovian buildMarkovian(StateSpaceNavigator environmentalDynamics,
			ProbabilityMassFunction initialDistribution) {
		return MarkovianBuilder.createHiddenMarkovModel()
				.createStateSpaceNavigator(environmentalDynamics)
				.withInitialStateDistribution(initialDistribution)
				.handleObservationsWith(OBS_DECORATOR)
				.build();
	}

	@Override
	public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
		return (PerceivableEnvironmentalState) determineNextSampleGiven((HiddenEnvironmentalState) last)
				.getObservation();
	}

	@Override
	public PerceivableEnvironmentalState determineInitial() {
		// TODO Could be better solved... see HiddenMarkovian
		return (PerceivableEnvironmentalState) sampler.drawInitialSample().getCurrent().getProduces().get(0);
	}

}
