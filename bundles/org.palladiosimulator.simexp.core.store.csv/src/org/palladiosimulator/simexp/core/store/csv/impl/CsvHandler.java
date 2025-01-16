package org.palladiosimulator.simexp.core.store.csv.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.ResourcesPlugin;

public abstract class CsvHandler {

    private final static String CSV_FILE_EXTENSION = ".csv";
    public final static String SAMPLE_SPACE_FILE = "SampleSpace" + CSV_FILE_EXTENSION;
    public final static String SIMULATED_EXPERIENCE_STORE_FILE = "SimulatedExperienceStore" + CSV_FILE_EXTENSION;

    public final static Path SIMULATED_EXPERIENCE_BASE_FOLDER = Paths.get(ResourcesPlugin.getWorkspace()
        .getRoot()
        .getLocation()
        .toString() + "/resource");

    public abstract void close();

}
