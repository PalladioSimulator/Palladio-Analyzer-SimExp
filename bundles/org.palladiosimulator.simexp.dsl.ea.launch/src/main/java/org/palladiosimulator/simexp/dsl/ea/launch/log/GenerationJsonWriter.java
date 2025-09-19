package org.palladiosimulator.simexp.dsl.ea.launch.log;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GenerationJsonWriter implements IEAEvolutionStatusReceiver {
    private static final Logger LOGGER = Logger.getLogger(GenerationJsonWriter.class);

    static class GenerationEntry {
        public final long generation;
        public final double reward;
        public final Map<String, Object> values;

        public GenerationEntry(long generation, double reward, List<OptimizableValue<?>> optimizableValues) {
            this.generation = generation;
            this.reward = reward;
            Map<String, Object> optimizables = new TreeMap<>();
            for (OptimizableValue<?> ov : optimizableValues) {
                optimizables.put(ov.getOptimizable()
                    .getName(), ov.getValue());
            }
            this.values = optimizables;
        }
    }

    private final Path jsonPath;
    private final List<GenerationEntry> entries;
    private final Gson gson;

    public GenerationJsonWriter(Path csvFolder) {
        this.jsonPath = csvFolder.resolve("generations.json");
        this.entries = new ArrayList<>();
        gson = new GsonBuilder() //
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();
    }

    @Override
    public void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues, double fitness) {
        GenerationEntry entry = new GenerationEntry(generation, fitness, optimizableValues);
        entries.add(entry);

        try (Writer writer = Files.newBufferedWriter(jsonPath)) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
