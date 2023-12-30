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

public abstract class SelfAdaptiveSystemStateSpaceNavigator<S, A> extends InductiveStateSpaceNavigator<S, A> {

    public interface InitialSelfAdaptiveSystemStateCreator<S, A> {

        public SelfAdaptiveSystemState<S, A> create(ArchitecturalConfiguration<S, A> initialArch,
                PerceivableEnvironmentalState initialEnv);

        public ArchitecturalConfiguration<S, A> getInitialArchitecturalConfiguration();
    }

    protected final EnvironmentProcess environmentalDynamics;

    protected SelfAdaptiveSystemStateSpaceNavigator(EnvironmentProcess environmentalDynamics) {
        this.environmentalDynamics = environmentalDynamics;
    }

    public ProbabilityMassFunction<State<S>> createInitialDistribution(
            InitialSelfAdaptiveSystemStateCreator<S, A> sassCreator) {
        return new ProbabilityMassFunction<>() {

            @Override
            public Sample<State<S>> drawSample() {
                ArchitecturalConfiguration<S, A> initialArch = sassCreator.getInitialArchitecturalConfiguration();
                PerceivableEnvironmentalState initialEnv = determineInitial(initialArch);
                SelfAdaptiveSystemState<S, A> create = sassCreator.create(initialArch, initialEnv);
                return Sample.of(create);
            }

            @Override
            public double probability(Sample<State<S>> sample) {
                return 0;
            }
        };
    }

    @Override
    public State<S> navigate(NavigationContext<S, A> context) {
        NavigationContextValidator checkContext = this.new NavigationContextValidator();
        try {
            checkContext.validate(context);
        } catch (SelfAdaptiveSystemStateSpaceNavigator.NavigationContextValidator.NavigationContextValidationExcpetion e) {
            throw new RuntimeException(e);
        }

        SelfAdaptiveSystemState<S, A> structuralState = determineStructuralState(context);
        return determineQuantifiedState(structuralState);
    }

    private class NavigationContextValidator {
        public void validate(NavigationContext<S, A> context) throws NavigationContextValidationExcpetion {
            boolean isValid = true;
            StringBuilder invalidContxtMsg = new StringBuilder("Context is invalid. Reason: ");

            Optional<Action<A>> action = context.getAction();

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

        public static class NavigationContextValidationExcpetion extends Exception {
            private static final long serialVersionUID = 1L;

            public NavigationContextValidationExcpetion(String message) {
                super(message);
            }
        }
    }

    private SelfAdaptiveSystemState<S, A> determineQuantifiedState(SelfAdaptiveSystemState<S, A> structuralState) {
        Optional<SimulatedExperience> result = SimulatedExperienceStore.get()
            .findSelfAdaptiveSystemState(structuralState.toString());
        if (result.isPresent()) {
            return RestoredSelfAdaptiveSystemState.restoreFrom(result.get(), structuralState);
        }
        structuralState.determineQuantifiedState();
        return structuralState;
    }

    protected abstract SelfAdaptiveSystemState<S, A> determineStructuralState(NavigationContext<S, A> context);

    protected abstract PerceivableEnvironmentalState determineInitial(ArchitecturalConfiguration<S, A> initialArch);

}
