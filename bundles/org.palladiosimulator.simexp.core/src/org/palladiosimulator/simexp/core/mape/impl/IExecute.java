package org.palladiosimulator.simexp.core.mape.impl;

import org.palladiosimulator.simexp.core.mape.IStateManager;

 interface IExecute {
    void execute(IAction action, IStateManager sm);
}
