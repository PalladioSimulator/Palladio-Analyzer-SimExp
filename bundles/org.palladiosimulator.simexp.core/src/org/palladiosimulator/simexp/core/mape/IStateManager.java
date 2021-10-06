package org.palladiosimulator.simexp.core.mape;

public interface IStateManager {
    
    State getCurrent();
    
    void newState(State s);
}
