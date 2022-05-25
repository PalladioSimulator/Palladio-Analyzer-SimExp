package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import org.palladiosimulator.simexp.core.entity.StateAwareSimulatedExperience;

abstract class MonteCaroEstimator implements BiConsumer<Set<String>, List<StateAwareSimulatedExperience>> {

    @Override
    public void accept(Set<String> t, List<StateAwareSimulatedExperience> u) {
        estimate(t, u);
    }

    public abstract void estimate(Set<String> t, List<StateAwareSimulatedExperience> u);
}
