package org.palladiosimulator.simexp.core.mape.impl;

import org.palladiosimulator.simexp.core.mape.IStateManager;
import org.palladiosimulator.simexp.core.mape.State;

 class AnAction implements IAction {

    public AnAction() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void action(IStateManager sm) {
        sm.newState(State.Two);
    }

}
