package org.palladiosimulator.simexp.model.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class ModelledReconfigurationStrategy extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {
    // private final Kmodel kmodel;

    public ModelledReconfigurationStrategy(Kmodel kmodel) {
        // this.kmodel = kmodel;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        // TODO Auto-generated method stub
        return null;
    }
}
