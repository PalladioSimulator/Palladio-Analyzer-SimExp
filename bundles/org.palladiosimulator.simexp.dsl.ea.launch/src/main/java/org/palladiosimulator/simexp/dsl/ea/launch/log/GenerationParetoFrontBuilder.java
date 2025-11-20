package org.palladiosimulator.simexp.dsl.ea.launch.log;

import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.launch.io.JsonResultWriter;
import org.palladiosimulator.simexp.dsl.ea.pareto.ParetoFrontBuilder;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

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
        List<IndividualResult> paretoFront = paretoFrontBuilder.buildParetoFront(population);
        JsonResultWriter jsonParetoWriter = new JsonResultWriter();
        jsonParetoWriter.storeIndividualResults(generationFile, paretoFront);
    }

    @Override
    public void close() {
    }
}
