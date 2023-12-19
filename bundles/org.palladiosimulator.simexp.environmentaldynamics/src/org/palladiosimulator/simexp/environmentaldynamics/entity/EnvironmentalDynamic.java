package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface EnvironmentalDynamic {

    public static class EnvironmentalDynamicBuilder<T> {

        private MarkovModel<T> model = null;
        private DerivableEnvironmentalDynamic dynamic = null;
        private boolean exploration = false;
        private boolean isHiddenProcess = false;

        private EnvironmentalDynamicBuilder(MarkovModel<T> model) {
            this.model = model;
        }

        private EnvironmentalDynamicBuilder(DerivableEnvironmentalDynamic dynamic) {
            this.dynamic = dynamic;
        }

        public EnvironmentalDynamicBuilder<T> asExplorationProcess() {
            this.exploration = true;
            return this;
        }

        public EnvironmentalDynamicBuilder<T> asExploitationProcess() {
            this.exploration = false;
            return this;
        }

        public EnvironmentalDynamicBuilder<T> isHiddenProcess() {
            this.isHiddenProcess = true;
            return this;
        }

        public EnvironmentalDynamic build() {
            StateSpaceNavigator navigator;
            if (model != null) {
                navigator = initDescribable(exploration);
            } else {
                navigator = initDerivable(exploration);
            }
            return EnvironmentalDynamic.class.cast(navigator);
        }

        private StateSpaceNavigator initDerivable(boolean isExploration) {
            Objects.requireNonNull(dynamic, "The environemtal dynamic must not be null.");

            dynamic.isHiddenProcess = isHiddenProcess;
            if (isExploration) {
                dynamic.pursueExplorationStrategy();
            } else {
                dynamic.pursueExploitationStrategy();
            }
            return dynamic;
        }

        private StateSpaceNavigator initDescribable(boolean isExploration) {
            DescribableEnvironmentalDynamic<T> navigator = new DescribableEnvironmentalDynamic<T>(model,
                    isHiddenProcess, isExploration);
            return navigator;
        }

    }

    public static <T> EnvironmentalDynamicBuilder<T> describedBy(MarkovModel<T> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    public static <T> EnvironmentalDynamicBuilder<T> derivedBy(MarkovModel<T> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    // public void pursueExplorationStrategy();

    // public void pursueExploitationStrategy();

    public boolean isHiddenProcess();

}
