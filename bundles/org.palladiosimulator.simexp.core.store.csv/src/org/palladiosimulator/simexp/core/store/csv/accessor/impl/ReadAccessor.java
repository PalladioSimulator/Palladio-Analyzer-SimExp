package org.palladiosimulator.simexp.core.store.csv.accessor.impl;

import static org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter.startingWith;
import static org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter.withSameId;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvHandler;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvReadHandler;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvSimulatedExperience;

public class ReadAccessor implements SimulatedExperienceReadAccessor {
    private final Path resourceFolder;

    private CsvReadHandler csvSampleReadHandler = null;
    private CsvReadHandler csvStoreReadHandler = null;

    public ReadAccessor(Path resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    @Override
    public void connect(SimulatedExperienceStoreDescription desc) {
        Path csvStoreFile = resourceFolder.resolve(CsvHandler.SIMULATED_EXPERIENCE_STORE_FILE);
        Path csvSampleSpaceFile = resourceFolder.resolve(CsvHandler.SAMPLE_SPACE_FILE);
        csvSampleReadHandler = new CsvReadHandler(csvSampleSpaceFile);
        csvStoreReadHandler = new CsvReadHandler(csvStoreFile);
    }

    @Override
    public void close() {
        csvStoreReadHandler.close();
        csvSampleReadHandler.close();
    }

    @Override
    public Optional<SimulatedExperience> findSimulatedExperience(String id) {
        Optional<SimulatedExperience> result = queryStoreWithFull(id);
        return result;
    }

    @Override
    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        Optional<SimulatedExperience> result = queryStoreWithPrefix(id);
        return result;
    }

    @Override
    public boolean existTrajectoryAt(int index) {
        try {
            List<String> allRows = csvSampleReadHandler.getAllRows();
            // First line (header) has to be omitted
            index++;
            return index < allRows.size();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Optional<List<SimulatedExperience>> getTrajectoryAt(int index) {
        // First line (header) has to be omitted
        index++;

        try {
            List<String> allRows = csvSampleReadHandler.getAllRows();
            String rowAt = allRows.get(index);
            String[] row = rowAt.split(CsvFormatter.CSV_DELIMITER);
            return Optional.of(toTrajectory(row));
        } catch (NoSuchFileException e) {
            return Optional.empty();
        } catch (IndexOutOfBoundsException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
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
        List<String> lines;
        try {
            lines = csvStoreReadHandler.getAllRows();
        } catch (NoSuchFileException e) {
            lines = Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        Stream<String> stream = lines.stream();
        return stream.filter(criterion)
            .map(row -> CsvSimulatedExperience.of(row))
            .findFirst();
    }
}
