package org.palladiosimulator.simexp.dsl.ea.launch.pareto;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonResultWriter {
    private static final Logger LOGGER = Logger.getLogger(JsonResultWriter.class);

    private final Gson gson;

    public JsonResultWriter() {
        this.gson = new GsonBuilder() //
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    }

    public void storeIndividualResults(Path resultFile, List<IndividualResult> individualResults) {
        List<ResultEntry> entries = new ArrayList<>();
        for (IndividualResult result : individualResults) {
            Map<String, Object> optimizables = new TreeMap<>();
            for (OptimizableValue<?> ov : result.getOptimizableValues()) {
                optimizables.put(ov.getOptimizable()
                    .getName(), ov.getValue());
            }
            ResultEntry entry = new ResultEntry(result.getFitness(), optimizables);
            entries.add(entry);
        }
        try (Writer writer = Files.newBufferedWriter(resultFile)) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
