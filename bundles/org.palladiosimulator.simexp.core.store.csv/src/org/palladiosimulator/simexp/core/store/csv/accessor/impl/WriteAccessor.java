package org.palladiosimulator.simexp.core.store.csv.accessor.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceWriteAccessor;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvHandler;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvWriteHandler;

public class WriteAccessor implements SimulatedExperienceWriteAccessor {
    private static final Logger LOGGER = Logger.getLogger(WriteAccessor.class);

    private final SimulatedExperienceStoreDescription description;

    private CsvWriteHandler csvSampleWriteHandler = null;
    private CsvWriteHandler csvStoreWriteHandler = null;

    public WriteAccessor(SimulatedExperienceStoreDescription description) {
        this.description = description;
    }

    @Override
    public void connect(SimulatedExperienceStoreDescription desc) {
        Path csvFolder = CsvHandler.SIMULATED_EXPERIENCE_BASE_FOLDER.resolve(desc.getSimulationId());
        Path csvStoreFile = csvFolder.resolve(CsvHandler.SIMULATED_EXPERIENCE_STORE_FILE);
        Path csvSampleSpaceFile = csvFolder.resolve(CsvHandler.SAMPLE_SPACE_FILE);
        try {
            Files.createDirectories(csvFolder);
            String sampleSpaceHeader = CsvFormatter.formatSampleSpaceHeader(desc.getSampleHorizon());
            csvSampleWriteHandler = new CsvWriteHandler(csvSampleSpaceFile, sampleSpaceHeader);
            String storeHeader = CsvFormatter.formatSimulatedExperienceStoreHeader();
            csvStoreWriteHandler = new CsvWriteHandler(csvStoreFile, storeHeader);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
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
