package org.palladiosimulator.simexp.dsl.ea.launch.log;

import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.launch.io.JsonParetoResultWriter;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class GenerationDumper implements IEAEvolutionStatusReceiver {
    private final Path generationsPath;

    public GenerationDumper(Path generationsPath) {
        this.generationsPath = generationsPath;
    }

    @Override
    public void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues, double fitness,
            List<IndividualResult> population) {
        Path generationFile = generationsPath.resolve(String.format("generation_%03d.json", generation));

        JsonParetoResultWriter jsonParetoWriter = new JsonParetoResultWriter();
        jsonParetoWriter.storeIndividualResults(generationFile, population);
    }

    @Override
    public void close() {
    }
}
