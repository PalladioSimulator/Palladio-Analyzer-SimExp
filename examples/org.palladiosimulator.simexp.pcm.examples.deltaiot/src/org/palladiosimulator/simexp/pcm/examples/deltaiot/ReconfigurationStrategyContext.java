package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;

public interface ReconfigurationStrategyContext<A, Aa extends Reconfiguration<A>> {

    public Set<Aa> getReconfigurationSpace();

    public boolean isSelectionPolicy();

    public ReconfigurationStrategy<A, Aa> getStrategy();

    public Policy<A, Aa> getSelectionPolicy();

    public default String getStrategyID() {
        return isSelectionPolicy() ? getSelectionPolicy().getId() : getStrategy().getId();
    }
}
