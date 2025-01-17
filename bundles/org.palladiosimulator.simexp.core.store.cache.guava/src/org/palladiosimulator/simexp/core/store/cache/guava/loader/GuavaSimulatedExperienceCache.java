package org.palladiosimulator.simexp.core.store.cache.guava.loader;

import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GuavaSimulatedExperienceCache {

    private final static long CACHE_SIZE = 1000;

    private final LoadingCache<String, SimulatedExperience> cache;

    public GuavaSimulatedExperienceCache() {
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(new CacheLoader<String, SimulatedExperience>() {

                @Override
                public SimulatedExperience load(String id) throws Exception {
                    // TODO exception handling
                    throw new Exception();
                }
            });
    }

    public Optional<SimulatedExperience> load(String id) {
        try {
            return Optional.of(cache.get(id));
        } catch (Exception e) {
            // TODO logging
            return Optional.empty();
        }
    }

    public void put(String id, SimulatedExperience simulatedExperience) {
        cache.put(id, simulatedExperience);
    }

}
