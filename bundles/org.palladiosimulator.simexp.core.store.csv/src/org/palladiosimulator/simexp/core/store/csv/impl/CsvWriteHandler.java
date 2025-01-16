package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public class CsvWriteHandler extends CsvHandler {

    private final Path csvFile;
    private final PrintWriter csvWriter;

    private CsvWriteHandler(String folder, String file) throws IOException {
        this.csvFile = createCsvFile(folder, file).toPath();
        csvWriter = createCsvWriter(csvFile.toFile());
    }

    private CsvWriteHandler(Path csvFile) {
        this.csvFile = csvFile;
        csvWriter = loadCsvWriterWithAppendMode(csvFile.toFile());
    }

    public static CsvWriteHandler create(String folder, String file) throws IOException {
        return new CsvWriteHandler(folder, file);
    }

    public static CsvWriteHandler load(File csvFile) {
        return new CsvWriteHandler(csvFile.toPath());
    }

    private PrintWriter createCsvWriter(File csvFile) {
        return loadCsvWriterWithoutAppendMode(csvFile);
    }

    private PrintWriter loadCsvWriterWithoutAppendMode(File csvFile) {
        return loadCsvWriter(csvFile, false);
    }

    private PrintWriter loadCsvWriterWithAppendMode(File csvFile) {
        return loadCsvWriter(csvFile, true);
    }

    private PrintWriter loadCsvWriter(File csvFile, boolean append) {
        try {
            BufferedWriter csvWritter = new BufferedWriter(new FileWriter(csvFile, append));
            return new PrintWriter(csvWritter);
        } catch (IOException e) {
            // TODO exception handling
            throw new RuntimeException("", e);
        }
    }

    public void append(List<SimulatedExperience> trajectory) {
        append(CsvFormatter.format(trajectory));
    }

    public void append(SimulatedExperience simulatedExperience) {
        append(CsvFormatter.format(simulatedExperience));
    }

    public void append(String value) {
        csvWriter.println(value);
    }

    @Override
    public void close() {
        csvWriter.close();
    }

}
