package org.palladiosimulator.simexp.dsl.ea.launch.log;

import java.nio.file.Path;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualParetoResult;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.launch.io.JsonResultWriter;
import org.palladiosimulator.simexp.dsl.ea.launch.io.ResultEntry;
import org.palladiosimulator.simexp.dsl.ea.pareto.ParetoFrontBuilder;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GenerationParetoFrontBuilder implements IEAEvolutionStatusReceiver {
    private static final Logger LOGGER = Logger.getLogger(GenerationParetoFrontBuilder.class);

    private final Path generationsPath;
    private final IQualityAttributeProvider qualityAttributeProvider;
    private final IPrecisionProvider precisionProvider;

    public GenerationParetoFrontBuilder(Path generationsPath, IQualityAttributeProvider qualityAttributeProvider,
            IPrecisionProvider precisionProvider) {
        this.generationsPath = generationsPath;
        this.qualityAttributeProvider = qualityAttributeProvider;
        this.precisionProvider = precisionProvider;
    }

    @Override
    public void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues, double fitness,
            List<IndividualResult> population) {
        Path generationFile = generationsPath.resolve(String.format("pareto_front_%03d.json", generation));
        ParetoFrontBuilder paretoFrontBuilder = new ParetoFrontBuilder(qualityAttributeProvider, precisionProvider);
        List<IndividualParetoResult> paretoFront = paretoFrontBuilder.buildParetoFront(population);
        JsonResultWriter jsonParetoWriter = new JsonResultWriter();
        try {
            jsonParetoWriter.storeIndividualParetoResults(generationFile, paretoFront);
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            Gson gson = new GsonBuilder() //
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .serializeSpecialFloatingPointValues()
                .setPrettyPrinting()
                .create();
            StringBuilder sb = new StringBuilder();
            try {
                sb.append("population:\n");
                List<ResultEntry> populationEntries = jsonParetoWriter.extractResultEntries(population);
                sb.append(gson.toJson(populationEntries));
            } catch (Exception e2) {
                sb.append("exception2: " + e2.getMessage());
            }
            LOGGER.error(sb.toString());
            sb = new StringBuilder();
            try {
                sb.append("pareto front:\n");
                List<ResultEntry> paretoEntries = jsonParetoWriter.extractParetoEntries(paretoFront);
                sb.append(gson.toJson(paretoEntries));
            } catch (Exception e3) {
                sb.append("exception3: " + e3.getMessage());
            }
            LOGGER.error(sb.toString());
            throw new RuntimeException("failed to write pareto front", e);
        }
    }

    @Override
    public void close() {
    }
}
