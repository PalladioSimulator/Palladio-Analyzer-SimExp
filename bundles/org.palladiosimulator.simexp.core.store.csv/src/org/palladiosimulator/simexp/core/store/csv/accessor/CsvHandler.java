package org.palladiosimulator.simexp.core.store.csv.accessor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.resources.ResourcesPlugin;

public abstract class CsvHandler {

	private final static String CSV_FILE_EXTENSION = ".csv";
	protected final static String EMPTY_STRING = "";
	protected final static String SIMULATED_EXPERIENCE_BASE_FOLDER = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/resource";
	protected final static String SAMPLE_SPACE_FILE = "SampleSpace";
	protected final static String SIMULATED_EXPERIENCE_STORE_FILE = "SimulatedExperienceStore";
	
	public static File loadCsvFile(String folder, String file) {
		StringBuilder builder = new StringBuilder();
		builder.append(concatPathSegments(SIMULATED_EXPERIENCE_BASE_FOLDER, folder, file));
		builder.append(CSV_FILE_EXTENSION);
		return new File(builder.toString());
	}
	
	public static File loadOrCreate(String folder, String file) {
		File csvFolder = loadOrCreateFolder(folder);
		File csvFile = new File(concatPathSegments(csvFolder.getAbsolutePath(), file + CSV_FILE_EXTENSION));
		if (csvFile.exists()) {
			return csvFile;
		}
		return create(csvFile);
	}
	
	private static File create(File csvFile) {
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			// TODO Exception handling
			new RuntimeException(e);
		}
		return csvFile;
	}

	public static File createCsvFile(String folder, String file) throws IOException {
		File csvFolder = loadOrCreateFolder(folder);
		File csvFile = new File(concatPathSegments(csvFolder.getAbsolutePath(), file + CSV_FILE_EXTENSION));
		if (csvFile.exists() == false) {
			csvFile.createNewFile();
		}
		return csvFile;
	}
	
	private static File loadOrCreateFolder(String folder) {
		File csvFolder = new File(concatPathSegments(SIMULATED_EXPERIENCE_BASE_FOLDER, folder));
		if (csvFolder.exists() == false) {
			csvFolder.mkdirs();
		}
		return csvFolder;
	}
	
	protected static String constructSampleSpaceFileName(String filePrefix) {
		return filePrefix + SAMPLE_SPACE_FILE;
	}
	
	private static String concatPathSegments(String...segments) {
		if (segments.length == 1) {
			return segments[0];
		}
		String[] remaining = Arrays.copyOfRange(segments, 1, segments.length);
		return concatPathSegments(segments[0], concatPathSegments(remaining));
	}
	
	private static String concatPathSegments(String first, String second) {
		return String.format("%1s/%2s", first, second);
	}
	
	public abstract void close();
	
}
