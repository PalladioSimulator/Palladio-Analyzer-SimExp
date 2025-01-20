package org.palladiosimulator.simexp.core.store.csv.impl;

import static org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter.CSV_DELIMITER;

import java.util.Arrays;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.csv.impl.CsvFormatter.SimulatedExperienceStoreColumnName;

public class CsvSimulatedExperience implements SimulatedExperience {

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

    public void setReward(String reward) {
        this.reward = reward;
    }

}
