package org.palladiosimulator.simexp.core.store.csv.impl;

import java.nio.file.Path;

public abstract class CsvHandler {
    protected final Path csvFile;

    public CsvHandler(Path csvFile) {
        this.csvFile = csvFile;
    }

}
