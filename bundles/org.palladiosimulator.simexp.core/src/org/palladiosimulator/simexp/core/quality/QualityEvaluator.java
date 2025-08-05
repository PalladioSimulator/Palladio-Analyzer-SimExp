package org.palladiosimulator.simexp.core.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator;

public class QualityEvaluator implements IQualityEvaluator {
    private final List<SimulatedMeasurementSpecification> measurementSpecs;

    public QualityEvaluator(List<? extends SimulatedMeasurementSpecification> measurementSpecs) {
        this.measurementSpecs = new ArrayList<>(measurementSpecs);
    }

    @Override
    public List<Map<String, List<Double>>> getQualityAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

}
