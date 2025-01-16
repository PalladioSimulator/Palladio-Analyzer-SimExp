package org.palladiosimulator.simexp.core.store.csv.accessor;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceCache;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceWriteAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.ReadAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.WriteAccessor;

public class CsvAccessor implements SimulatedExperienceAccessor {
    private static final Logger LOGGER = Logger.getLogger(CsvAccessor.class);

    private Optional<SimulatedExperienceCache> cache = Optional.empty();

    @Override
    public void setOptionalCache(SimulatedExperienceCache cache) {
        this.cache = Optional.of(cache);
    }

    @Override
    public SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor() {
        return new WriteAccessor();
    }

    @Override
    public SimulatedExperienceReadAccessor createSimulatedExperienceReadAccessor() {
        ReadAccessor readAccessor = new ReadAccessor();
        if (cache.isPresent()) {
            readAccessor.setOptionalCache(cache.get());
        }
        return readAccessor;
    }
}
