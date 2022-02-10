package org.palladiosimulator.simexp.core.statespace;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.RestoredSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
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
        NavigationContextValidator checkContext = this.new NavigationContextValidator();
        try {
            checkContext.validate(context);
        } catch (SelfAdaptiveSystemStateSpaceNavigator.NavigationContextValidator.NavigationContextValidationExcpetion e) {
            throw new RuntimeException(e);
        }
		
		SelfAdaptiveSystemState<?> structuralState = determineStructuralState(context);
		return determineQuantifiedState(structuralState);
	}
	
	
    private class NavigationContextValidator {
        public void validate(NavigationContext context) throws NavigationContextValidationExcpetion {
            boolean isValid = true;
            StringBuilder invalidContxtMsg = new StringBuilder("Context is invalid. Reason: ");

            Optional<Action<?>> action = context.getAction();

            if (!action.isPresent()) {
                invalidContxtMsg.append("no action present");
                isValid = false;
            } else if (!(action.get() instanceof Reconfiguration)) {
                invalidContxtMsg.append("specified action does not conform to reconfiguration specification");
                isValid = false;
            } else if (!(context.getSource() instanceof SelfAdaptiveSystemState)) {
                invalidContxtMsg.append("specified state does not conform to self-adaptive system state specification");
                isValid = false;
            }
            if (!isValid) {
                throw new NavigationContextValidationExcpetion(invalidContxtMsg.toString());
            }
        }

        public class NavigationContextValidationExcpetion extends Exception {
            public NavigationContextValidationExcpetion(String message) {
                super(message);
            }
        }
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
