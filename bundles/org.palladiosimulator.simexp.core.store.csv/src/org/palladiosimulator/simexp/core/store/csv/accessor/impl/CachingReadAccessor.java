package org.palladiosimulator.simexp.core.store.csv.accessor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;

public class CachingReadAccessor implements SimulatedExperienceReadAccessor {
    private final SimulatedExperienceReadAccessor delegate;
    private final Map<String, SimulatedExperience> cache = new HashMap<>();

    public CachingReadAccessor(SimulatedExperienceReadAccessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void connect() {
        delegate.connect();
    }

    @Override
    public void close() {
        cache.clear();
        delegate.close();
    }

    @Override
    public Optional<SimulatedExperience> findSimulatedExperience(String id) {
        Optional<SimulatedExperience> simulatedExperience = Optional.ofNullable(cache.get(id));
        if (simulatedExperience.isEmpty()) {
            simulatedExperience = delegate.findSimulatedExperience(id);
            if (simulatedExperience.isPresent()) {
                cache.put(id, simulatedExperience.get());
            }
        }
        return simulatedExperience;
    }

    @Override
    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        Optional<SimulatedExperience> simulatedExperience = Optional.ofNullable(cache.get(id));
        if (simulatedExperience.isEmpty()) {
            simulatedExperience = delegate.findSelfAdaptiveSystemState(id);
            if (simulatedExperience.isPresent()) {
                cache.put(id, simulatedExperience.get());
            }
        }
        return simulatedExperience;
    }

    @Override
    public boolean existTrajectoryAt(int iteration) {
        return delegate.existTrajectoryAt(iteration);
    }

    @Override
    public Optional<List<SimulatedExperience>> getTrajectoryAt(int index) {
        return delegate.getTrajectoryAt(index);
    }

}
