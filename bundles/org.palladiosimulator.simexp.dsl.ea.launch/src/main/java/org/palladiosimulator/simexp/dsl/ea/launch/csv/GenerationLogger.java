package org.palladiosimulator.simexp.dsl.ea.launch.csv;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class GenerationLogger implements IEAEvolutionStatusReceiver {
    private static final Logger LOGGER = Logger.getLogger(GenerationLogger.class);
    private static final String[] HEADERS = { "Generation", "Reward", "Values" };

    private final Path csvPath;

    public GenerationLogger(Path csvFolder) {
        this.csvPath = csvFolder.resolve("generations.csv");
    }

    @Override
    public void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues, double fitness) {
        CSVFormat csvFormat = CSVFormat.newFormat(';')
            .withRecordSeparator("\r\n");
        if (!Files.exists(csvPath)) {
            csvFormat = csvFormat.withHeader(HEADERS);
        }
        try (Writer writer = Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
                OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
                String values = optimizableValueToString.asString(optimizableValues);
                printer.printRecord(generation, fitness, values);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
