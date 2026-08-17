package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result.csv;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultHandler;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CsvResultLogger implements IResultHandler {
    private static final Logger LOGGER = Logger.getLogger(CsvResultLogger.class);
    private static final String[] HEADERS = { "TaskId", "Values", "Reward", "Error", "ExecutorId" };

    private final Path csvPath;

    public CsvResultLogger(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public synchronized void process(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        CSVFormat csvFormat = CSVFormat.newFormat(';')
            .withRecordSeparator("\r\n");
        if (!Files.exists(csvPath)) {
            csvFormat = csvFormat.withHeader(HEADERS);
        }
        try (Writer writer = Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
                OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
                String values = optimizableValueToString.asString(optimizableValues);
                printer.printRecord(result.id, values, result.reward, result.error, result.executor_id);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void dispose() {
    }
}
