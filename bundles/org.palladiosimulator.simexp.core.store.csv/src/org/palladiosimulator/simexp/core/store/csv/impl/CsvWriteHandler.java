package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CsvWriteHandler extends CsvHandler {

    private final Path csvFile;
    private final String header;

    public CsvWriteHandler(Path csvFile, String header) {
        this.csvFile = csvFile;
        this.header = header;
    }

    public void append(String value) throws IOException {
        final OpenOption option;
        if (!Files.exists(csvFile)) {
            option = StandardOpenOption.CREATE;
        } else {
            option = StandardOpenOption.APPEND;
        }
        try (PrintWriter csvWritter = new PrintWriter(Files.newBufferedWriter(csvFile, option))) {
            if (option == StandardOpenOption.CREATE) {
                csvWritter.println(header);
            }
            csvWritter.println(value);
        }
    }

    @Override
    public void close() {
    }
}
