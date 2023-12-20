package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.simexp.markovian.activity.BasePolicy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;
import org.palladiosimulator.simexp.markovian.statespace.ActionBasedDeductiveNavigator;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.PolicyBasedDeductiveNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public class StateSpaceNavigatorBuilder {

    public static class InductiveStateSpaceNavigatorBuilder<S, A> {

        private InductiveStateSpaceNavigator<S, A> inductiveNav;

        public InductiveStateSpaceNavigatorBuilder<S, A> inductiveNavigationThrough(
                InductiveStateSpaceNavigator<S, A> inductiveNav) {
            this.inductiveNav = inductiveNav;
            return this;
        }

        public StateSpaceNavigator<S, A> build() {
            // TODO exception handling
            Objects.requireNonNull(inductiveNav, "");

            return inductiveNav;
        }

    }

    public static class DeductiveStateSpaceNavigatorBuilder<S, A, R> {

        private MarkovModel<S, A, R> markovModel;
        private Optional<BasePolicy<S, Transition<S, A>>> policy = Optional.empty();

        public DeductiveStateSpaceNavigatorBuilder(MarkovModel<S, A, R> markovModel) {
            this.markovModel = markovModel;
        }

        public DeductiveStateSpaceNavigatorBuilder<S, A, R> withTransitionPolicy(
                BasePolicy<S, Transition<S, A>> policy) {
            this.policy = Optional.ofNullable(policy);
            return this;
        }

        public StateSpaceNavigator<S, A> build() {
            // TODO exception handling
            Objects.requireNonNull(markovModel, "");

            if (policy.isPresent()) {
                return new PolicyBasedDeductiveNavigator<S, A, R>(markovModel, policy.get());
            }
            return new ActionBasedDeductiveNavigator<S, A, R>(markovModel);
        }

    }

    public static <S, A> InductiveStateSpaceNavigatorBuilder<S, A> createStateSpaceNavigator() {
        return new InductiveStateSpaceNavigatorBuilder<>();
    }

    public static <S, A, R> DeductiveStateSpaceNavigatorBuilder<S, A, R> createStateSpaceNavigator(
            MarkovModel<S, A, R> markovModel) {
        return new DeductiveStateSpaceNavigatorBuilder<>(markovModel);
    }
}
