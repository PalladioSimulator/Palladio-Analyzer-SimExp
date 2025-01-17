package org.palladiosimulator.simexp.core.store.csv.accessor.impl;

import java.nio.file.Path;

public class BaseAccessor {
    private final static String CSV_FILE_EXTENSION = ".csv";
    protected final static String SAMPLE_SPACE_FILE = "SampleSpace" + CSV_FILE_EXTENSION;
    protected final static String SIMULATED_EXPERIENCE_STORE_FILE = "SimulatedExperienceStore" + CSV_FILE_EXTENSION;

    protected final Path resourceFolder;

    public BaseAccessor(Path resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

}
