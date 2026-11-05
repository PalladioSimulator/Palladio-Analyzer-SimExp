package org.palladiosimulator.simexp.dsl.ea.launch.io;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.dispatcher.io.JsonResultWriter;
import org.palladiosimulator.simexp.dsl.dispatcher.io.ResultEntry;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualParetoResult;

public class JsonParetoResultWriter extends JsonResultWriter {
    public void storeIndividualParetoResults(Path resultFile, List<IndividualParetoResult> individualParetoResults) {
        List<ResultEntry> entries = extractParetoEntries(individualParetoResults);
        writeEntries(resultFile, entries);
    }

    public List<ResultEntry> extractParetoEntries(List<IndividualParetoResult> individualParetoResults) {
        List<ResultEntry> entries = new ArrayList<>();
        for (IndividualParetoResult paretoResult : individualParetoResults) {
            IndividualResult result = paretoResult.getIndividualResult();
            Map<String, Object> optimizables = extractOptimizables(result);
            Map<String, Double> averages = new TreeMap<>(paretoResult.getAverages());
            ParetoEntry entry = new ParetoEntry(result.getFitness(), optimizables, result.getId(),
                    paretoResult.buildScore(), averages);
            entries.add(entry);
        }
        return entries;
    }
}
