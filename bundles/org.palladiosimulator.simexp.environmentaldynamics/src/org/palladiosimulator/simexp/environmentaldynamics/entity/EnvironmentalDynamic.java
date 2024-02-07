package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface EnvironmentalDynamic {

    public static class EnvironmentalDynamicBuilder<A, Aa extends Action<A>, R> {

        private MarkovModel<A, R> model = null;
        private DerivableEnvironmentalDynamic<A> dynamic = null;
        private boolean exploration = false;
        private boolean isHiddenProcess = false;

        private EnvironmentalDynamicBuilder(MarkovModel<A, R> model) {
            this.model = model;
        }

        private EnvironmentalDynamicBuilder(DerivableEnvironmentalDynamic<A> dynamic) {
            this.dynamic = dynamic;
        }

        public EnvironmentalDynamicBuilder<A, Aa, R> asExplorationProcess() {
            this.exploration = true;
            return this;
        }

        public EnvironmentalDynamicBuilder<A, Aa, R> asExploitationProcess() {
            this.exploration = false;
            return this;
        }

        public EnvironmentalDynamicBuilder<A, Aa, R> isHiddenProcess() {
            this.isHiddenProcess = true;
            return this;
        }

        public EnvironmentalDynamic build() {
            StateSpaceNavigator<A> navigator;
            if (model != null) {
                navigator = initDescribable(exploration);
            } else {
                navigator = initDerivable(exploration);
            }
            return EnvironmentalDynamic.class.cast(navigator);
        }

        private StateSpaceNavigator<A> initDerivable(boolean isExploration) {
            Objects.requireNonNull(dynamic, "The environemtal dynamic must not be null.");

            dynamic.isHiddenProcess = isHiddenProcess;
            if (isExploration) {
                dynamic.pursueExplorationStrategy();
            } else {
                dynamic.pursueExploitationStrategy();
            }
            return dynamic;
        }

        private StateSpaceNavigator<A> initDescribable(boolean isExploration) {
            DescribableEnvironmentalDynamic<A, Aa, R> navigator = new DescribableEnvironmentalDynamic<>(model,
                    isHiddenProcess, isExploration);
            return navigator;
        }

    }

    public static <A, Aa extends Action<A>, R> EnvironmentalDynamicBuilder<A, Aa, R> describedBy(
            MarkovModel<A, R> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    public static <A, Aa extends Action<A>, R> EnvironmentalDynamicBuilder<A, Aa, R> derivedBy(
            MarkovModel<A, R> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    // public void pursueExplorationStrategy();

    // public void pursueExploitationStrategy();

    public boolean isHiddenProcess();

}
