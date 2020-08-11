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
	
	public UnobservableEnvironmentProcess(MarkovModel model, ProbabilityMassFunction initialDistribution) {
		super(model, initialDistribution);
	}
	
	public UnobservableEnvironmentProcess(DerivableEnvironmentalDynamic dynamics, ProbabilityMassFunction initialDistribution) {
		super(dynamics, initialDistribution);
	}

	@Override
	protected Markovian buildMarkovian(StateSpaceNavigator environmentalDynamics, ProbabilityMassFunction initialDistribution) {
		return MarkovianBuilder.createHiddenMarkovModel()
							   .createStateSpaceNavigator(environmentalDynamics)
							   .withInitialStateDistribution(initialDistribution)
							   .handleObservationsWith(getObservationProducer())
							   .build();
	}

	@Override
	public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
		return (PerceivableEnvironmentalState) determineNextSampleGiven((HiddenEnvironmentalState) last).getObservation();
	}

	private ObservationProducer getObservationProducer() {
		return new ObservationProducer() {
			
			@Override
			public Observation<?> produceObservationGiven(State emittingState) {
				if (isHiddenState(emittingState)) {
					return ((HiddenEnvironmentalState) emittingState).produceObservation();
				}
				
				//TODO exception handling
				throw new RuntimeException("");
			}
			
			private boolean isHiddenState(State state) {
				return state instanceof HiddenEnvironmentalState;
			}
			
		};
	}

	@Override
	public PerceivableEnvironmentalState determineInitial() {
		//TODO Could be better solved... see HiddenMarkovian
		return (PerceivableEnvironmentalState) sampler.drawInitialSample().getCurrent().getProduces().get(0);
	}
	
}
