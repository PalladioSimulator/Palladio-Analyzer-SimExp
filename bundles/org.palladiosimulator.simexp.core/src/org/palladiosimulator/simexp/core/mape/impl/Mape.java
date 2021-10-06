package org.palladiosimulator.simexp.core.mape.impl;

import org.palladiosimulator.simexp.core.mape.IKB;
import org.palladiosimulator.simexp.core.mape.IMAPE;
import org.palladiosimulator.simexp.core.mape.IStateManager;

public class Mape implements IMAPE {
    
    private final IKB kb;
    
    private IMonitor monitor;
    private IAnalyze analyze;
    private IPlan plan;
    private IExecute execute;

    public Mape(IKB kb) {
        this.kb = kb;
    }

    @Override
    public void executeDecisionProcess(IStateManager sm) {
        monitor.monitor(kb);
        analyze.analyze(kb);
        IAction action = plan.plan(kb);
        execute.execute(action, sm);
    }
}
