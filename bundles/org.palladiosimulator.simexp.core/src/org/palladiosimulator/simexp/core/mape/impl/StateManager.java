package org.palladiosimulator.simexp.core.mape.impl;

import org.palladiosimulator.simexp.core.mape.IStateManager;
import org.palladiosimulator.simexp.core.mape.State;

public class StateManager implements IStateManager {
    private State state;

    @Override
    public State getCurrent() {
        return state;
    }

    @Override
    public void newState(State state) {
        this.state = state;
    }

}
