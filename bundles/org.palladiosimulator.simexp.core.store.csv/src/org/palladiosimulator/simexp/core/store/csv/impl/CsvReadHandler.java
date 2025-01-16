package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CsvReadHandler extends CsvHandler {

    private Path csvFile;

    public CsvReadHandler(Path csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public void close() {
        csvFile = null;
    }

    public List<String> getAllRows() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
            return reader.lines()
                .collect(Collectors.toList());
        }
    }

    public String getRowAt(int index) throws IndexOutOfBoundsException, IOException {
        return getAllRows().get(index);
    }

    public boolean isEmptyFile() throws IOException {
        return getAllRows().size() == 0;
    }

}
