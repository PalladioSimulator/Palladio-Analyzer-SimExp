package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface EnvironmentalDynamic {

    public static class EnvironmentalDynamicBuilder<S, A, Aa extends Action<A>, R> {

        private MarkovModel<S, A, R> model = null;
        private DerivableEnvironmentalDynamic<S, A> dynamic = null;
        private boolean exploration = false;
        private boolean isHiddenProcess = false;

        private EnvironmentalDynamicBuilder(MarkovModel<S, A, R> model) {
            this.model = model;
        }

        private EnvironmentalDynamicBuilder(DerivableEnvironmentalDynamic<S, A> dynamic) {
            this.dynamic = dynamic;
        }

        public EnvironmentalDynamicBuilder<S, A, Aa, R> asExplorationProcess() {
            this.exploration = true;
            return this;
        }

        public EnvironmentalDynamicBuilder<S, A, Aa, R> asExploitationProcess() {
            this.exploration = false;
            return this;
        }

        public EnvironmentalDynamicBuilder<S, A, Aa, R> isHiddenProcess() {
            this.isHiddenProcess = true;
            return this;
        }

        public EnvironmentalDynamic build() {
            StateSpaceNavigator<S, A> navigator;
            if (model != null) {
                navigator = initDescribable(exploration);
            } else {
                navigator = initDerivable(exploration);
            }
            return EnvironmentalDynamic.class.cast(navigator);
        }

        private StateSpaceNavigator<S, A> initDerivable(boolean isExploration) {
            Objects.requireNonNull(dynamic, "The environemtal dynamic must not be null.");

            dynamic.isHiddenProcess = isHiddenProcess;
            if (isExploration) {
                dynamic.pursueExplorationStrategy();
            } else {
                dynamic.pursueExploitationStrategy();
            }
            return dynamic;
        }

        private StateSpaceNavigator<S, A> initDescribable(boolean isExploration) {
            DescribableEnvironmentalDynamic<S, A, Aa, R> navigator = new DescribableEnvironmentalDynamic<>(model,
                    isHiddenProcess, isExploration);
            return navigator;
        }

    }

    public static <S, A, Aa extends Action<A>, R> EnvironmentalDynamicBuilder<S, A, Aa, R> describedBy(
            MarkovModel<S, A, R> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    public static <S, A, Aa extends Action<A>, R> EnvironmentalDynamicBuilder<S, A, Aa, R> derivedBy(
            MarkovModel<S, A, R> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    // public void pursueExplorationStrategy();

    // public void pursueExploitationStrategy();

    public boolean isHiddenProcess();

}
