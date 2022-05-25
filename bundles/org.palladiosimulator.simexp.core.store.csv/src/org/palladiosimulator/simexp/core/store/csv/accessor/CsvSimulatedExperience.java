package org.palladiosimulator.simexp.core.store.csv.accessor;

import static org.palladiosimulator.simexp.core.store.csv.accessor.CsvFormatter.CSV_DELIMITER;

import java.util.Arrays;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.entity.StateAwareSimulatedExperience;
import org.palladiosimulator.simexp.core.store.csv.accessor.CsvFormatter.SimulatedExperienceStoreColumnName;

public class CsvSimulatedExperience implements StateAwareSimulatedExperience {

	private String reward;
	private final List<String> row;
	
	private CsvSimulatedExperience(String row, String reward) {
		this.reward = reward;
		this.row = Arrays.asList(row.split(CSV_DELIMITER));
	}
	
	public static SimulatedExperience of(String row) {
		return new CsvSimulatedExperience(row, "");
	}
	
	@Override
	public String getConfigurationDifferenceBefore() {
		return row.get(SimulatedExperienceStoreColumnName.CONFIGURATION_BEFOR.getIndex());
	}

	@Override
	public String getConfigurationDifferenceAfter() {
		return row.get(SimulatedExperienceStoreColumnName.CONFIGURATION_AFTER.getIndex());
	}

	@Override
	public String getReconfiguration() {
		return row.get(SimulatedExperienceStoreColumnName.RECONFIGURATION.getIndex());
	}

	@Override
	public String getQuantifiedStateOfCurrent() {
		return row.get(SimulatedExperienceStoreColumnName.QUANTIFIED_STATE_CURRENT.getIndex());
	}

	@Override
	public String getQuantifiedStateOfNext() {
		return row.get(SimulatedExperienceStoreColumnName.QUANTIFIED_STATE_NEXT.getIndex());
	}

	@Override
	public String getEnvironmentalStateBefore() {
		return row.get(SimulatedExperienceStoreColumnName.ENVIRONMENTAL_STATE_BEFORE.getIndex());
	}

	@Override
	public String getEnvironmentalStateAfter() {
		return row.get(SimulatedExperienceStoreColumnName.ENVIRONMENTAL_STATE_AFTER.getIndex());
	}

	@Override
	public String getEnvironmentalStateObservation() {
		return row.get(SimulatedExperienceStoreColumnName.ENVIRONMENTAL_STATE_OBSERVATION.getIndex());
	}

	@Override
	public String getId() {
		return row.get(SimulatedExperienceStoreColumnName.ID.getIndex());
	}

	@Override
	public String getReward() {
		return reward;
	}

	protected void setReward(String reward) {
		this.reward = reward;
	}

	@Override
    public String getCurrentState() {
        String reconf = getReconfiguration();
        String currentState = getId().split(reconf)[0];  // split Id column at 'reconfig name' and take the first substring
        CharSequence subSeq = currentState.subSequence(0, currentState.lastIndexOf("_")); // remove trailing underscore
        return subSeq.toString();
	}
}
