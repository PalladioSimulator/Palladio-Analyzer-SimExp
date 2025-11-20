package org.palladiosimulator.simexp.dsl.ea.launch.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualParetoResult;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
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
            Map<String, Object> optimizables = extractOptimizables(result);
            ResultEntry entry = new ResultEntry(result.getFitness(), optimizables);
            entries.add(entry);
        }
        writeEntries(resultFile, entries);
    }

    public void storeIndividualParetoResults(Path resultFile, List<IndividualParetoResult> individualParetoResults) {
        List<ResultEntry> entries = new ArrayList<>();
        for (IndividualParetoResult paretoResult : individualParetoResults) {
            IndividualResult result = paretoResult.getIndividualResult();
            Map<String, Object> optimizables = extractOptimizables(result);
            Map<String, Double> averages = new TreeMap<>(paretoResult.getAverages());
            ParetoEntry entry = new ParetoEntry(result.getFitness(), optimizables, paretoResult.buildScore(), averages);
            entries.add(entry);
        }
        writeEntries(resultFile, entries);
    }

    private Map<String, Object> extractOptimizables(IndividualResult result) {
        Map<String, Object> optimizables = new TreeMap<>();
        for (OptimizableValue<?> ov : result.getOptimizableValues()) {
            optimizables.put(ov.getOptimizable()
                .getName(), ov.getValue());
        }
        return optimizables;
    }

    private void writeEntries(Path resultFile, List<ResultEntry> entries) {
        try (Writer writer = Files.newBufferedWriter(resultFile)) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
