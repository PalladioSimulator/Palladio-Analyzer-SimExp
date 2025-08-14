package org.palladiosimulator.simexp.dsl.ea.launch.pareto;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.launch.pareto.ParetoFront.ParetoEntry;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonParetoWriter {
    private static final Logger LOGGER = Logger.getLogger(JsonParetoWriter.class);

    private final Path paretoFrontFile;
    private final Gson gson;

    public JsonParetoWriter(Path paretoFrontFile) {
        this.paretoFrontFile = paretoFrontFile;
        this.gson = new GsonBuilder() //
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    }

    public void storeParetoFront(List<IndividualResult> paretoFrontList) {
        List<ParetoEntry> entries = new ArrayList<>();
        for (IndividualResult result : paretoFrontList) {
            Map<String, Object> optimizables = new HashMap<>();
            for (OptimizableValue<?> ov : result.getOptimizableValues()) {
                optimizables.put(ov.getOptimizable()
                    .getName(), ov.getValue());
            }
            ParetoEntry entry = new ParetoEntry(result.getFitness(), optimizables);
            entries.add(entry);
        }
        ParetoFront paretoFront = new ParetoFront(entries);
        try (Writer writer = Files.newBufferedWriter(paretoFrontFile)) {
            gson.toJson(paretoFront, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
