package org.palladiosimulator.simexp.core.store.csv.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class CsvReadHandler extends CsvHandler {

	private File csvFile;
	
	private CsvReadHandler(String folder, String file) {
		try {
			csvFile = createCsvFile(folder, file);
		} catch (IOException e) {
			// TODO Exception handling
			throw new RuntimeException("");
		}
	}

	private CsvReadHandler(File csvFile) {
		this.csvFile = csvFile;
	}
	
	public static CsvReadHandler load(File csvFile) {
		return new CsvReadHandler(csvFile);
	}
	
	public static CsvReadHandler create(String folder, String file) {
		return new CsvReadHandler(folder, file);
	}
	
	private BufferedReader getReader() {
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(csvFile.toPath());
		} catch (IOException e) {
			// TODO exception handling
			new RuntimeException("");
		}
		return reader;
	}

	@Override
	public void close() {
		csvFile = null;
	}
	
	public List<String> getAllRows() {
		BufferedReader reader = getReader();
		try {
			return reader.lines().collect(Collectors.toList());
		} finally {
			close(reader);
		}
	}
	
	private void close(Reader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			// TODO logging
		}
	}

	public String getRowAt(int index) throws IndexOutOfBoundsException {
		return getAllRows().get(index);
	}
	
	public boolean isEmptyFile() {
		return getAllRows().size() == 0;
	}
	
}
