package org.palladiosimulator.simexp.core.store.csv.accessor;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceWriteAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.CachingReadAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.ReadAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.WriteAccessor;

public class CsvAccessor implements SimulatedExperienceAccessor {
    @Override
    public SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor() {
        return new WriteAccessor();
    }

    @Override
    public SimulatedExperienceReadAccessor createSimulatedExperienceReadAccessor() {
        SimulatedExperienceReadAccessor readAccessor = new CachingReadAccessor(new ReadAccessor());
        return readAccessor;
    }
}
