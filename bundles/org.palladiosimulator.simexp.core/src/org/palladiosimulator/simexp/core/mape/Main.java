package org.palladiosimulator.simexp.core.mape;

import org.palladiosimulator.simexp.core.mape.impl.Mape;
import org.palladiosimulator.simexp.core.mape.impl.StateManager;

public class Main {

    public Main() {
        IKB kb = null;
        IMAPE mape = new Mape(kb);
        IStateManager sm = new StateManager();

        while (true) {
            mape.executeDecisionProcess(sm);
            if (whatever()) {
                break;
            }
        }
        State current = sm.getCurrent();
    }

    private boolean whatever() {
        // TODO Auto-generated method stub
        return false;
    }
}
