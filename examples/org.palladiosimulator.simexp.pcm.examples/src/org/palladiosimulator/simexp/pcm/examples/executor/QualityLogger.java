package org.palladiosimulator.simexp.pcm.examples.executor;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.Run;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QualityLogger implements IQualityLogger {
    private static final Logger LOGGER = Logger.getLogger(QualityLogger.class);
    private final Path qaPath;
    private final List<? extends SimulatedMeasurementSpecification> measurementSpecs;
    private final Gson gson;

    private int current_run = 0;
    private Map<String, List<Double>> qualityAttributes;

    public QualityLogger(Path qaPath, List<? extends SimulatedMeasurementSpecification> measurementSpecs) {
        this.qaPath = qaPath;
        this.measurementSpecs = measurementSpecs;
        this.gson = new GsonBuilder() //
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    }

    @Override
    public void initialize() {
        if (qualityAttributes != null) {
            Run run = new Run(qualityAttributes);
            QualityMeasurements qualityMeasurements = new QualityMeasurements(Collections.singletonList(run));
            save_qas(current_run, qualityMeasurements);
        }
        qualityAttributes = new HashMap<>();
        current_run++;
    }

    @Override
    public void dispose() {
        if ((qualityAttributes != null) && !qualityAttributes.isEmpty()) {
            Run run = new Run(qualityAttributes);
            QualityMeasurements qualityMeasurements = new QualityMeasurements(Collections.singletonList(run));
            save_qas(current_run, qualityMeasurements);
        }
    }

    static class RunEntry {
        public final int run;
        public final QualityMeasurements qualityMeasurements;

        public RunEntry(int run, QualityMeasurements qualityMeasurements) {
            this.run = run;
            this.qualityMeasurements = qualityMeasurements;
        }
    }

    private void save_qas(int run, QualityMeasurements qualityMeasurements) {
        Path taskPath = buildFilePath(run);
        RunEntry runEntry = new RunEntry(run, qualityMeasurements);
        try (Writer writer = Files.newBufferedWriter(taskPath)) {
            gson.toJson(runEntry, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private Path buildFilePath(int run) {
        String fileName = String.format("Run_%d.json", run);
        Path taskPath = qaPath.resolve(fileName);
        return taskPath;
    }

    @Override
    public void monitor(State state) {
        SelfAdaptiveSystemState<?, ?, ?> sasState = (SelfAdaptiveSystemState<?, ?, ?>) state;
        for (SimulatedMeasurementSpecification measurementSpec : measurementSpecs) {
            StateQuantity quantifiedState = sasState.getQuantifiedState();
            SimulatedMeasurement measurement = quantifiedState.findMeasurementWith(measurementSpec)
                .orElseThrow();
            String measurementName = measurementSpec.getName();
            double measuredValue = measurement.getValue();

            List<Double> entries = qualityAttributes.get(measurementName);
            if (entries == null) {
                entries = new ArrayList<>();
                qualityAttributes.put(measurementName, entries);
            }
            entries.add(measuredValue);
        }
    }
}
