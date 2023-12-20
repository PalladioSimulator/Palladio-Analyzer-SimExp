package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class StaticSystemSimulation<S> extends ReconfigurationStrategy<S, QVTOReconfigurator, QVToReconfiguration> {

    @Override
    public String getId() {
        return "StaticSystemSimulation";
    }

    @Override
    protected void monitor(State<S> source, SharedKnowledge knowledge) {

    }

    @Override
    protected boolean analyse(State<S> source, SharedKnowledge knowledge) {
        return false;
    }

    @Override
    protected QVToReconfiguration plan(State<S> source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        return emptyReconfiguration();
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return QVToReconfiguration.empty();
    }

}
