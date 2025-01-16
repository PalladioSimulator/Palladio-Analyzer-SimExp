package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.ResourcesPlugin;

public abstract class CsvHandler {

    public final static String CSV_FILE_EXTENSION = ".csv";
    public final static Path SIMULATED_EXPERIENCE_BASE_FOLDER = Paths.get(ResourcesPlugin.getWorkspace()
        .getRoot()
        .getLocation()
        .toString() + "/resource");
    public final static String SAMPLE_SPACE_FILE = "SampleSpace";
    public final static String SIMULATED_EXPERIENCE_STORE_FILE = "SimulatedExperienceStore";

    public static File loadOrCreate(Path csvFolder, String file) throws IOException {
        Path csvFile = csvFolder.resolve(file + CSV_FILE_EXTENSION);
        return csvFile.toFile();
    }

    public File createCsvFile(String folder, String file) throws IOException {
        Path csvFolder = loadOrCreateFolder(folder);
        Path csvFile = csvFolder.resolve(file + CSV_FILE_EXTENSION);
        return csvFile.toFile();
    }

    private static Path loadOrCreateFolder(String folder) throws IOException {
        Path csvFolder = SIMULATED_EXPERIENCE_BASE_FOLDER.resolve(folder);
        Files.createDirectories(csvFolder);
        return csvFolder;
    }

    public abstract void close();

}
