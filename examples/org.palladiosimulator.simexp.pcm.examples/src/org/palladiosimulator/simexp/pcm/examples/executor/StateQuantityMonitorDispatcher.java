package org.palladiosimulator.simexp.pcm.examples.executor;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.markovian.activity.StateQuantityMonitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class StateQuantityMonitorDispatcher implements StateQuantityMonitor {
    private final List<StateQuantityMonitor> delegates = new ArrayList<>();

    public void addStateQuantityMonitor(StateQuantityMonitor monitor) {
        delegates.add(monitor);
    }

    @Override
    public void monitor(State state) {
        delegates.stream()
            .forEach(d -> d.monitor(state));
    }
}
