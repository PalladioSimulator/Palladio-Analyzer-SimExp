package org.palladiosimulator.simexp.core.store.csv.accessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public class CsvFormatter {

	public enum SimulatedExperienceStoreColumnName {
		ID(0),
		ENVIRONMENTAL_STATE_BEFORE(1),
		QUANTIFIED_STATE_CURRENT(2),
		CONFIGURATION_BEFOR(3),
		RECONFIGURATION(4),
		CONFIGURATION_AFTER(5),
		QUANTIFIED_STATE_NEXT(6),
		ENVIRONMENTAL_STATE_AFTER(7),
		ENVIRONMENTAL_STATE_OBSERVATION(8);
		
		private final int index;
		
		private SimulatedExperienceStoreColumnName(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
	}
	
	private static class RowFormatter {
		
		private final String delimiter;
		private final List<String> values;
		
		public RowFormatter(List<String> values) {
			this(values, CSV_DELIMITER);
		}
		
		public RowFormatter(List<String> values, String delimiter) {
			this.values = values;
			this.delimiter = delimiter;
		}
		
		public String toRow() {
			StringBuilder builder = new StringBuilder();
			for (String each : values) {
				builder.append(withDelimiter(each));
			}
			deleteLastDelimiter(builder);
			return builder.toString();
		}
		
		private String withDelimiter(String value) {
			return value + delimiter;
		}
		
		private void deleteLastDelimiter(StringBuilder builder) {
			builder.deleteCharAt(builder.lastIndexOf(delimiter));
		}
		
	}
	
	public final static String CSV_DELIMITER = ";";
	private final static String ID_TAG = "Id";
	private final static String TIME_TAG = "Point in time";
	private final static String ENVIRONMENTAL_STATE_BEFORE_TAG = "Environmental state before";
	private final static String ENVIRONMENTAL_STATE_AFTER_TAG = "Environmental state after";
	private final static String ENVIRONMENTAL_STATE_OBSERVATION_TAG = "Environmental state observation";
	private final static String RECONFIGURATION_TAG = "Reconfiguration";
	private final static String CONFIGURATION_BEFOR_TAG = "Configuration before";
	private final static String CONFIGURATION_AFTER_TAG = "Configuration after";
	private final static String QUANTIFIED_STATE_CURRENT_TAG = "Quantified state current";
	private final static String QUANTIFIED_STATE_NEXT_TAG = "Quantified state next";
	private final static String REWARD_TAG = "Reward";
	
	public static String formatSampleSpaceHeader(int trajectoryLength) {
		List<String> values = new ArrayList<>();
		for (int i = 1; i < trajectoryLength; i++) {
			values.add(String.format("%1s: %2s", TIME_TAG, Integer.toString(i - 1)));
			values.add(REWARD_TAG);
		}
		return new RowFormatter(values).toRow();
	}
	
	public static String formatSimulatedExperienceStoreHeader() {
		List<String> values = Arrays.asList(ID_TAG, 
											ENVIRONMENTAL_STATE_BEFORE_TAG,
											QUANTIFIED_STATE_CURRENT_TAG,
											CONFIGURATION_BEFOR_TAG,
											RECONFIGURATION_TAG,
											CONFIGURATION_AFTER_TAG,
										    QUANTIFIED_STATE_NEXT_TAG,
										    ENVIRONMENTAL_STATE_AFTER_TAG,
										    ENVIRONMENTAL_STATE_OBSERVATION_TAG);
		return new RowFormatter(values).toRow();
	}
	
	public static String format(List<SimulatedExperience> trajectory) {
		List<String> values = new ArrayList<>();
		for (SimulatedExperience each : trajectory) {
			values.add(each.getId());
			values.add(each.getReward());
		}
		return new RowFormatter(values).toRow();
	}
	
	public static String format(SimulatedExperience simulatedExperience) {
		return new RowFormatter(toList(simulatedExperience)).toRow();
	}

	private static List<String> toList(SimulatedExperience simulatedExperience) {
		return Arrays.asList(simulatedExperience.getId(),
							 simulatedExperience.getEnvironmentalStateBefore(),
							 simulatedExperience.getQuantifiedStateOfCurrent(),
							 simulatedExperience.getConfigurationDifferenceBefore(),
							 simulatedExperience.getReconfiguration(),
						     simulatedExperience.getConfigurationDifferenceAfter(),
						     simulatedExperience.getQuantifiedStateOfNext(),
						     simulatedExperience.getEnvironmentalStateAfter(),
						     simulatedExperience.getEnvironmentalStateObservation());
	}
	
	public static Predicate<String> withSameId(String id) {
		return row -> getId(row).equals(id);
	}
	
	public static Predicate<String> startingWith(String id) {
		return row -> getId(row).startsWith(id);
	}
	
	private static String getId(String row) {
		return row.split(CSV_DELIMITER)[0];
	}
	
}
