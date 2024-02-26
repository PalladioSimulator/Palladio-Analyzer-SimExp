package org.palladiosimulator.simexp.model.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class ModelledReconfigurationStrategy extends ReconfigurationStrategy {

    public ModelledReconfigurationStrategy() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Reconfiguration emptyReconfiguration() {
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
    protected Reconfiguration plan(State source, Set options, SharedKnowledge knowledge) {
        // TODO Auto-generated method stub
        return null;
    }
}
