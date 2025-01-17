package org.palladiosimulator.simexp.core.store.csv.accessor.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceWriteAccessor;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvWriteHandler;

public class WriteAccessor extends BaseAccessor implements SimulatedExperienceWriteAccessor {
    private final Path resourceFolder;
    private final SimulatedExperienceStoreDescription description;

    private CsvWriteHandler csvSampleWriteHandler = null;
    private CsvWriteHandler csvStoreWriteHandler = null;

    public WriteAccessor(Path resourceFolder, SimulatedExperienceStoreDescription description) {
        this.description = description;
        this.resourceFolder = resourceFolder;
    }

    @Override
    public void connect(SimulatedExperienceStoreDescription desc) {
        Path csvStoreFile = resourceFolder.resolve(SIMULATED_EXPERIENCE_STORE_FILE);
        Path csvSampleSpaceFile = resourceFolder.resolve(SAMPLE_SPACE_FILE);
        String sampleSpaceHeader = CsvFormatter.formatSampleSpaceHeader(desc.getSampleHorizon());
        csvSampleWriteHandler = new CsvWriteHandler(csvSampleSpaceFile, sampleSpaceHeader);
        String storeHeader = CsvFormatter.formatSimulatedExperienceStoreHeader();
        csvStoreWriteHandler = new CsvWriteHandler(csvStoreFile, storeHeader);
    }

    @Override
    public void close() {
        csvStoreWriteHandler.close();
        csvSampleWriteHandler.close();
    }

    @Override
    public void store(SimulatedExperience simulatedExperience) {
        connect(description);
        try {
            String line = CsvFormatter.format(simulatedExperience);
            csvStoreWriteHandler.append(line);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close();
        }
    }

    @Override
    public void store(List<SimulatedExperience> trajectory) {
        connect(description);
        try {
            String line = CsvFormatter.format(trajectory);
            csvSampleWriteHandler.append(line);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close();
        }
    }
}
