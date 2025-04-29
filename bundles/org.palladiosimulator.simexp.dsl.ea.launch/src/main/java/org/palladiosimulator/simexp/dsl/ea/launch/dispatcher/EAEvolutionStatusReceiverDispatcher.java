package org.palladiosimulator.simexp.dsl.ea.launch.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class EAEvolutionStatusReceiverDispatcher implements IEAEvolutionStatusReceiver {
    private final List<IEAEvolutionStatusReceiver> receivers;

    public EAEvolutionStatusReceiverDispatcher() {
        this.receivers = new ArrayList<>();
    }

    public synchronized void addReceiver(IEAEvolutionStatusReceiver receiver) {
        this.receivers.add(receiver);
    }

    @Override
    public void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues, double fitness) {
        final List<IEAEvolutionStatusReceiver> receiversCopy;
        synchronized (this) {
            receiversCopy = new ArrayList<>(receivers);
        }

        receiversCopy.stream()
            .forEach(r -> r.reportStatus(generation, optimizableValues, fitness));
    }
}
