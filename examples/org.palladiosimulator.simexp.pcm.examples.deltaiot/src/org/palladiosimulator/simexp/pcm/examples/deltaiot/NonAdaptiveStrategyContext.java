package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.NonAdaptiveReconfigurationStrategy;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import com.google.common.collect.Sets;

public class NonAdaptiveStrategyContext
        implements ReconfigurationStrategyContext<QVTOReconfigurator, QVToReconfiguration> {

    private final ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> nonAdaptiveStrategy = new NonAdaptiveReconfigurationStrategy();

    @Override
    public Set<QVToReconfiguration> getReconfigurationSpace() {
        return Sets.newHashSet();
    }

    @Override
    public boolean isSelectionPolicy() {
        return false;
    }

    @Override
    public ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> getStrategy() {
        return nonAdaptiveStrategy;
    }

    @Override
    public Policy<QVTOReconfigurator, QVToReconfiguration> getSelectionPolicy() {
        return null;
    }

}
