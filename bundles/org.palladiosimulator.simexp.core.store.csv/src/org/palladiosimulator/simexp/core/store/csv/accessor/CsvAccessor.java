package org.palladiosimulator.simexp.core.store.csv.accessor;

import static org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter.startingWith;
import static org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter.withSameId;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceCache;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvHandler;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvReadHandler;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvSimulatedExperience;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvWriteHandler;

public class CsvAccessor implements SimulatedExperienceAccessor {

    private Optional<SimulatedExperienceCache> cache = Optional.empty();

    private CsvWriteHandler csvSampleWriteHandler = null;
    private CsvWriteHandler csvStoreWriteHandler = null;
    private CsvReadHandler csvSampleReadHandler = null;
    private CsvReadHandler csvStoreReadHandler = null;

    @Override
    public void connect(SimulatedExperienceStoreDescription desc) {
        File csvStoreFile = getCsvFile(desc.getSimulationId(), CsvHandler.SIMULATED_EXPERIENCE_STORE_FILE);
        if (csvStoreFile.exists()) {
            File csvSampleSpaceFile = CsvHandler.loadOrCreate(desc.getSimulationId(),
                    constructSampleSpaceFileName(desc.getSampleSpaceId()));
            csvSampleWriteHandler = CsvWriteHandler.load(csvSampleSpaceFile);
            csvSampleReadHandler = CsvReadHandler.load(csvSampleSpaceFile);
            csvStoreWriteHandler = CsvWriteHandler.load(csvStoreFile);
            csvStoreReadHandler = CsvReadHandler.load(csvStoreFile);
        } else {
            csvSampleWriteHandler = CsvWriteHandler.create(desc.getSimulationId(),
                    constructSampleSpaceFileName(desc.getSampleSpaceId()));
            csvSampleReadHandler = CsvReadHandler.create(desc.getSimulationId(),
                    constructSampleSpaceFileName(desc.getSampleSpaceId()));
            csvStoreWriteHandler = CsvWriteHandler.create(desc.getSimulationId(),
                    CsvHandler.SIMULATED_EXPERIENCE_STORE_FILE);
            csvStoreReadHandler = CsvReadHandler.create(desc.getSimulationId(),
                    CsvHandler.SIMULATED_EXPERIENCE_STORE_FILE);
        }

        if (csvSampleReadHandler.isEmptyFile()) {
            csvSampleWriteHandler.append(CsvFormatter.formatSampleSpaceHeader(desc.getSampleHorizon()));
        }

        if (csvStoreReadHandler.isEmptyFile()) {
            csvStoreWriteHandler.append(CsvFormatter.formatSimulatedExperienceStoreHeader());
        }
    }

    private File getCsvFile(String folder, String file) {
        StringBuilder builder = new StringBuilder();
        builder.append(concatPathSegments(CsvHandler.SIMULATED_EXPERIENCE_BASE_FOLDER, folder, file));
        builder.append(CsvHandler.CSV_FILE_EXTENSION);
        return new File(builder.toString());
    }

    private String concatPathSegments(String... segments) {
        if (segments.length == 1) {
            return segments[0];
        }
        String[] remaining = Arrays.copyOfRange(segments, 1, segments.length);
        return concatPathSegments(segments[0], concatPathSegments(remaining));
    }

    private String constructSampleSpaceFileName(String filePrefix) {
        return filePrefix + CsvHandler.SAMPLE_SPACE_FILE;
    }

    @Override
    public void store(SimulatedExperienceStoreDescription description, SimulatedExperience simulatedExperience) {
        // TODO Exception handling
        connect(description);
        try {
            Objects.requireNonNull(csvStoreWriteHandler, "");
            Objects.requireNonNull(csvSampleWriteHandler, "");

            csvStoreWriteHandler.append(simulatedExperience);
        } finally {
            close();
        }
    }

    @Override
    public void store(SimulatedExperienceStoreDescription description, List<SimulatedExperience> trajectory) {
        connect(description);
        try {
            // TODO Exception handling
            Objects.requireNonNull(csvStoreWriteHandler, "");
            Objects.requireNonNull(csvSampleWriteHandler, "");

            csvSampleWriteHandler.append(trajectory);
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        csvStoreReadHandler.close();
        csvStoreWriteHandler.close();
        csvSampleWriteHandler.close();
    }

    @Override
    public Optional<SimulatedExperience> findSimulatedExperience(String id) {
        // TODO Exception handling
        Objects.requireNonNull(csvStoreReadHandler, "");
        Optional<SimulatedExperience> result = queryCache(id);
        if (result.isPresent()) {
            return result;
        }

        result = queryStoreWithFull(id);
        result.ifPresent(s -> putInCache(id, s));
        return result;
    }

    @Override
    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        Objects.requireNonNull(csvStoreReadHandler, "");
        Optional<SimulatedExperience> result = queryCache(id);
        if (result.isPresent()) {
            return result;
        }

        result = queryStoreWithPrefix(id);
        result.ifPresent(s -> putInCache(id, s));
        return result;
    }

    @Override
    public Optional<List<SimulatedExperience>> getTrajectoryAt(int index) {
        // TODO Exception handling
        Objects.requireNonNull(csvSampleReadHandler, "");

        // First line (header) has to be omitted
        index++;

        try {
            String[] row = csvSampleReadHandler.getRowAt(index)
                .split(CsvFormatter.CSV_DELIMITER);
            return Optional.of(toTrajectory(row));
        } catch (IndexOutOfBoundsException e) {
            // TODO logging
            return Optional.empty();
        }
    }

    private List<SimulatedExperience> toTrajectory(String[] row) {
        List<SimulatedExperience> traj = new ArrayList<>();
        for (int i = 0; i < row.length; i += 2) {
            String simExpId = row[i];
            String reward = row[i + 1];
            traj.add(restoreSimulatedExperience(simExpId, reward));
        }
        return traj;
    }

    private SimulatedExperience restoreSimulatedExperience(String simExpId, String reward) {
        CsvSimulatedExperience simExp = (CsvSimulatedExperience) findSimulatedExperience(simExpId)
            .orElseThrow(() -> new RuntimeException(""));
        simExp.setReward(reward);
        return simExp;
    }

    private Optional<SimulatedExperience> queryStoreWithFull(String id) {
        return queryForSimulatedExperience(withSameId(id));
    }

    private Optional<SimulatedExperience> queryStoreWithPrefix(String id) {
        return queryForSimulatedExperience(startingWith(id));
    }

    private Optional<SimulatedExperience> queryForSimulatedExperience(Predicate<String> criterion) {
        Stream<String> lines = csvStoreReadHandler.getAllRows()
            .stream();
        return lines.filter(criterion)
            .map(row -> CsvSimulatedExperience.of(row))
            .findFirst();
    }

    private Optional<SimulatedExperience> queryCache(String id) {
        if (cache.isPresent()) {
            return cache.get()
                .load(id);
        }
        return Optional.empty();
    }

    private void putInCache(String id, SimulatedExperience simulatedExperience) {
        cache.ifPresent(c -> c.put(id, simulatedExperience));
    }

    @Override
    public void setOptionalCache(SimulatedExperienceCache cache) {
        this.cache = Optional.of(cache);
    }

}
