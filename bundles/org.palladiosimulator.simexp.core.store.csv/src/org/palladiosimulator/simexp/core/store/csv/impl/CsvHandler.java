package org.palladiosimulator.simexp.core.store.csv.impl;

public abstract class CsvHandler {

    private final static String CSV_FILE_EXTENSION = ".csv";
    public final static String SAMPLE_SPACE_FILE = "SampleSpace" + CSV_FILE_EXTENSION;
    public final static String SIMULATED_EXPERIENCE_STORE_FILE = "SimulatedExperienceStore" + CSV_FILE_EXTENSION;

    public abstract void close();

}
