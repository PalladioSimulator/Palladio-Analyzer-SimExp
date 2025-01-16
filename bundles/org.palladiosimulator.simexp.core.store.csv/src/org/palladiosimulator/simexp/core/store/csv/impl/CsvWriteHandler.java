package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public class CsvWriteHandler extends CsvHandler {
	
	private final PrintWriter csvWriter;
	
	private CsvWriteHandler(String folder, String file) {
		csvWriter = createCsvWriter(folder, file);
	}
	
	private CsvWriteHandler(File csvFile) {
		csvWriter = loadCsvWriterWithAppendMode(csvFile);
	}
	
	public static CsvWriteHandler create(String folder, String file) {
		return new CsvWriteHandler(folder, file);
	}
	
	public static CsvWriteHandler load(File csvFile) {
		return new CsvWriteHandler(csvFile);
	}
	
	private PrintWriter createCsvWriter(String folder, String file) {
		try { 
			File csvFile = createCsvFile(folder, file);
			return loadCsvWriterWithoutAppendMode(csvFile);
		} catch (IOException e) {
			//TODO exception handling
			throw new RuntimeException("", e);
		}
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
			//TODO exception handling
			throw new RuntimeException("", e);
		}
	}
	
	public void append(List<SimulatedExperience> trajectory) {
		csvWriter.println(CsvFormatter.format(trajectory));
	}
	
	public void append(SimulatedExperience simulatedExperience) {
		csvWriter.println(CsvFormatter.format(simulatedExperience));
	}
	
	public void append(String value) {
		csvWriter.println(value);
	}
	
	@Override
	public void close() {
		csvWriter.close();
	}
	
}
