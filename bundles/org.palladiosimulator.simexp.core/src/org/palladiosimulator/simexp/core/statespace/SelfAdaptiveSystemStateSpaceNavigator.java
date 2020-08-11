package org.palladiosimulator.simexp.core.statespace;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.RestoredSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class SelfAdaptiveSystemStateSpaceNavigator extends InductiveStateSpaceNavigator {

	public interface InitialSelfAdaptiveSystemStateCreator {

		public SelfAdaptiveSystemState<?> create(ArchitecturalConfiguration<?> initialArch, PerceivableEnvironmentalState initialEnv);

		public ArchitecturalConfiguration<?> getInitialArchitecturalConfiguration();
	}
	
	protected final EnvironmentProcess environmentalDynamics;
	
	protected SelfAdaptiveSystemStateSpaceNavigator(EnvironmentProcess environmentalDynamics) {
		this.environmentalDynamics = environmentalDynamics;
	}
	
	public static ObservationProducer getEnvironmentPerceiptionHandler() {
		return new ObservationProducer() {
			
			@Override
			public Observation<?> produceObservationGiven(State emittingState) {
				PerceivableEnvironmentalState perceivedState = ((SelfAdaptiveSystemState<?>) emittingState).getPerceivedEnvironmentalState();
				if (perceivedState.isHidden()) {
					return (EnvironmentalStateObservation) perceivedState;
				}
				
				//TODO exception handling
				throw new RuntimeException("");
			}
		};
	}
	
	public ProbabilityMassFunction createInitialDistribution(InitialSelfAdaptiveSystemStateCreator sassCreator) {
		return new ProbabilityMassFunction() {
			
			@Override
			public Sample drawSample() {
				ArchitecturalConfiguration<?> initialArch = sassCreator.getInitialArchitecturalConfiguration();
				PerceivableEnvironmentalState initialEnv = determineInitial(initialArch);
				return Sample.of(sassCreator.create(initialArch, initialEnv));
			}
			
			@Override
			public double probability(Sample sample) {
				return 0;
			}
		};
	}
	
	@Override
	public State navigate(NavigationContext context) {
		if (isNotValid(context)) {
			//TODO exception handling
			throw new RuntimeException("");
		}
		
		SelfAdaptiveSystemState<?> structuralState = determineStructuralState(context);
		return determineQuantifiedState(structuralState);
	}
	
	private boolean isNotValid(NavigationContext context) {
		return (context.getAction().isPresent()) == false ||
			   (context.getAction().get() instanceof Reconfiguration<?>) == false ||
			   (context.getSource() instanceof SelfAdaptiveSystemState<?>) == false;
	}
	
	private SelfAdaptiveSystemState<?> determineQuantifiedState(SelfAdaptiveSystemState<?> structuralState) {
		Optional<SimulatedExperience> result = SimulatedExperienceStore.get().findSelfAdaptiveSystemState(structuralState.toString());
		if (result.isPresent()) {
			return RestoredSelfAdaptiveSystemState.restoreFrom(result.get(), structuralState);
		}
		structuralState.determineQuantifiedState();
		return structuralState;
	}
	
	protected abstract SelfAdaptiveSystemState<?> determineStructuralState(NavigationContext context);
	
	protected abstract PerceivableEnvironmentalState determineInitial(ArchitecturalConfiguration<?> initialArch);
	
}
