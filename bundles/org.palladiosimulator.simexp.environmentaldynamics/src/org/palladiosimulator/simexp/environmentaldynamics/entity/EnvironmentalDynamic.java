package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface EnvironmentalDynamic {

    public static class EnvironmentalDynamicBuilder<S, R> {

        private MarkovModel<S, Double, R> model = null;
        private DerivableEnvironmentalDynamic<S, Double> dynamic = null;
        private boolean exploration = false;
        private boolean isHiddenProcess = false;

        private EnvironmentalDynamicBuilder(MarkovModel<S, Double, R> model) {
            this.model = model;
        }

        private EnvironmentalDynamicBuilder(DerivableEnvironmentalDynamic<S, Double> dynamic) {
            this.dynamic = dynamic;
        }

        public EnvironmentalDynamicBuilder<S, R> asExplorationProcess() {
            this.exploration = true;
            return this;
        }

        public EnvironmentalDynamicBuilder<S, R> asExploitationProcess() {
            this.exploration = false;
            return this;
        }

        public EnvironmentalDynamicBuilder<S, R> isHiddenProcess() {
            this.isHiddenProcess = true;
            return this;
        }

        public EnvironmentalDynamic build() {
            StateSpaceNavigator<S, Double> navigator;
            if (model != null) {
                navigator = initDescribable(exploration);
            } else {
                navigator = initDerivable(exploration);
            }
            return EnvironmentalDynamic.class.cast(navigator);
        }

        private StateSpaceNavigator<S, Double> initDerivable(boolean isExploration) {
            Objects.requireNonNull(dynamic, "The environemtal dynamic must not be null.");

            dynamic.isHiddenProcess = isHiddenProcess;
            if (isExploration) {
                dynamic.pursueExplorationStrategy();
            } else {
                dynamic.pursueExploitationStrategy();
            }
            return dynamic;
        }

        private StateSpaceNavigator<S, Double> initDescribable(boolean isExploration) {
            DescribableEnvironmentalDynamic<S, Action<Double>, R> navigator = new DescribableEnvironmentalDynamic<>(
                    model, isHiddenProcess, isExploration);
            return navigator;
        }

    }

    public static <S, R> EnvironmentalDynamicBuilder<S, R> describedBy(MarkovModel<S, Double, R> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    public static <S, R> EnvironmentalDynamicBuilder<S, R> derivedBy(MarkovModel<S, Double, R> model) {
        return new EnvironmentalDynamicBuilder<>(model);
    }

    // public void pursueExplorationStrategy();

    // public void pursueExploitationStrategy();

    public boolean isHiddenProcess();

}
