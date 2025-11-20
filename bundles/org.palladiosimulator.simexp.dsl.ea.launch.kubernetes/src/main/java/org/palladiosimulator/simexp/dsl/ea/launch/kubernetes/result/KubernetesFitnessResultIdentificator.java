package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IFitnessResultIdentificator;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultHandler;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class KubernetesFitnessResultIdentificator implements IFitnessResultIdentificator, IResultHandler {
    private final Map<List<OptimizableValue<?>>, String> idMap;

    public KubernetesFitnessResultIdentificator() {
        this.idMap = new HashMap<>();
    }

    @Override
    public synchronized Optional<String> getIdentificator(List<OptimizableValue<?>> optimizableValues) {
        if (idMap.containsKey(optimizableValues)) {
            String id = idMap.get(optimizableValues);
            return Optional.ofNullable(id);
        }
        return null;
    }

    @Override
    public synchronized void process(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        if (result.status == Status.COMPLETE) {
            idMap.put(optimizableValues, result.id);
        } else {
            idMap.put(optimizableValues, null);
        }
    }

    @Override
    public synchronized void dispose() {
        idMap.clear();
    }
}
