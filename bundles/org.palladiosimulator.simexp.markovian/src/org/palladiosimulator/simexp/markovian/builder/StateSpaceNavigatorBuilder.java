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

    public static class InductiveStateSpaceNavigatorBuilder<A> {

        private InductiveStateSpaceNavigator<A> inductiveNav;

        public InductiveStateSpaceNavigatorBuilder<A> inductiveNavigationThrough(
                InductiveStateSpaceNavigator<A> inductiveNav) {
            this.inductiveNav = inductiveNav;
            return this;
        }

        public StateSpaceNavigator<A> build() {
            // TODO exception handling
            Objects.requireNonNull(inductiveNav, "");

            return inductiveNav;
        }

    }

    public static class DeductiveStateSpaceNavigatorBuilder<A, R> {

        private MarkovModel<A, R> markovModel;
        private Optional<BasePolicy<Transition<A>>> policy = Optional.empty();

        public DeductiveStateSpaceNavigatorBuilder(MarkovModel<A, R> markovModel) {
            this.markovModel = markovModel;
        }

        public DeductiveStateSpaceNavigatorBuilder<A, R> withTransitionPolicy(BasePolicy<Transition<A>> policy) {
            this.policy = Optional.ofNullable(policy);
            return this;
        }

        public StateSpaceNavigator<A> build() {
            // TODO exception handling
            Objects.requireNonNull(markovModel, "");

            if (policy.isPresent()) {
                return new PolicyBasedDeductiveNavigator<A, R>(markovModel, policy.get());
            }
            return new ActionBasedDeductiveNavigator<A, R>(markovModel);
        }

    }

    public static <A> InductiveStateSpaceNavigatorBuilder<A> createStateSpaceNavigator() {
        return new InductiveStateSpaceNavigatorBuilder<>();
    }

    public static <A, R> DeductiveStateSpaceNavigatorBuilder<A, R> createStateSpaceNavigator(
            MarkovModel<A, R> markovModel) {
        return new DeductiveStateSpaceNavigatorBuilder<>(markovModel);
    }
}
