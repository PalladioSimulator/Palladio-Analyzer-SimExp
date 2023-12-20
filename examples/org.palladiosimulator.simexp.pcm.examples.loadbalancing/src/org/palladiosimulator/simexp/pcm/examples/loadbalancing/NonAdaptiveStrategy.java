package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class NonAdaptiveStrategy<S> implements Policy<S, QVTOReconfigurator, QVToReconfiguration> {

    @Override
    public String getId() {
        return "NonAdaptiveStrategy";
    }

    @Override
    public QVToReconfiguration select(State<S> source, Set<QVToReconfiguration> options) {
        return QVToReconfiguration.empty();
    }

}
