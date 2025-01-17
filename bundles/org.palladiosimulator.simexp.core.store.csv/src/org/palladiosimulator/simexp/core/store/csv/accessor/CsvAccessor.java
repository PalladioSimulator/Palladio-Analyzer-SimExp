package org.palladiosimulator.simexp.core.store.csv.accessor;

import java.nio.file.Path;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceWriteAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.CachingReadAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.ReadAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.impl.WriteAccessor;

public class CsvAccessor implements SimulatedExperienceAccessor {
    private final Path resourceFolder;

    public CsvAccessor(Path resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    @Override
    public SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor(
            SimulatedExperienceStoreDescription desc) {
        return new WriteAccessor(resourceFolder, desc);
    }

    @Override
    public SimulatedExperienceReadAccessor createSimulatedExperienceReadAccessor() {
        ReadAccessor delegate = new ReadAccessor(resourceFolder);
        delegate.connect();
        SimulatedExperienceReadAccessor readAccessor = new CachingReadAccessor(delegate);
        return readAccessor;
    }
}
