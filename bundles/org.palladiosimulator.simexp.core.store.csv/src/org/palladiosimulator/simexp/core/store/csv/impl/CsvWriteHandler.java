package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public class CsvWriteHandler extends CsvHandler {

    private final Path csvFile;

    public CsvWriteHandler(Path csvFile) {
        this.csvFile = csvFile;
    }

    public void append(List<SimulatedExperience> trajectory) throws IOException {
        append(CsvFormatter.format(trajectory));
    }

    public void append(SimulatedExperience simulatedExperience) throws IOException {
        append(CsvFormatter.format(simulatedExperience));
    }

    public void append(String value) throws IOException {
        try (PrintWriter csvWritter = new PrintWriter(
                Files.newBufferedWriter(csvFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            csvWritter.println(value);
        }
    }

    @Override
    public void close() {
    }
}
