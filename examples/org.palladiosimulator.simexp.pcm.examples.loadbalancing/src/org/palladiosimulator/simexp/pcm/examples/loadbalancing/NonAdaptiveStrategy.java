package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class NonAdaptiveStrategy implements Policy<QVTOReconfigurator, QVToReconfiguration> {

    @Override
    public String getId() {
        return "NonAdaptiveStrategy";
    }

    @Override
    public QVToReconfiguration select(State source, Set<QVToReconfiguration> options) {
        return EmptyQVToReconfiguration.empty();
    }

}
