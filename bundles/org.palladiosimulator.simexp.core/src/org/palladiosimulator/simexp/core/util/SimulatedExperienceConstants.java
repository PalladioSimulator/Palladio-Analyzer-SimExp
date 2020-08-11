package org.palladiosimulator.simexp.core.util;

public class SimulatedExperienceConstants {

	private final static String SAMPLE_SPACE_DELIMITER = "_";
	
	public static String constructSampleSpaceId(String simulationId, String policyId) {
		return String.format("%1s%2s%3s", simulationId, SAMPLE_SPACE_DELIMITER, policyId);
	}
	
}
