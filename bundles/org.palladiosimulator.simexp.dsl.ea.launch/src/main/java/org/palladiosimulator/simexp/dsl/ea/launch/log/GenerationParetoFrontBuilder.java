package org.palladiosimulator.simexp.dsl.ea.launch.log;

import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualParetoResult;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.launch.io.JsonResultWriter;
import org.palladiosimulator.simexp.dsl.ea.pareto.ParetoFrontBuilder;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GenerationParetoFrontBuilder implements IEAEvolutionStatusReceiver {
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
        try {
            JsonResultWriter jsonParetoWriter = new JsonResultWriter();
            jsonParetoWriter.storeIndividualParetoResults(generationFile, paretoFront);
        } catch (IllegalArgumentException e) {
            Gson gson = new GsonBuilder() //
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .serializeSpecialFloatingPointValues()
                .setPrettyPrinting()
                .create();
            StringBuilder sb = new StringBuilder();
            try {
                sb.append("population:\n");
                sb.append(gson.toJson(population));
            } catch (Exception e2) {
                sb.append("exception2: " + e2.getMessage());
            }
            try {
                sb.append("\n");
                sb.append("pareto front:\n");
                sb.append(gson.toJson(paretoFront));
            } catch (Exception e3) {
                sb.append("exception3: " + e3.getMessage());
            }
            throw new RuntimeException(String.format("causing front:\n%s", sb.toString()), e);
        }
    }

    @Override
    public void close() {
    }
}
