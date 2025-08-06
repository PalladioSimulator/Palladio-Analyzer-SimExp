package org.palladiosimulator.simexp.core.quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.activity.StateQuantityMonitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class QualityEvaluator implements IQualityEvaluator, StateQuantityMonitor, Initializable {
    private static final Logger LOGGER = Logger.getLogger(QualityEvaluator.class);

    private final List<SimulatedMeasurementSpecification> measurementSpecs;

    private List<Run> runs = new ArrayList<>();
    private Map<String, List<Double>> currentRun = null;

    public QualityEvaluator(List<? extends SimulatedMeasurementSpecification> measurementSpecs) {
        this.measurementSpecs = new ArrayList<>(measurementSpecs);
    }

    @Override
    public QualityMeasurements getQualityMeasurements() {
        if (currentRun != null) {
            runs.add(new Run(currentRun));
        }
        currentRun = null;
        return new QualityMeasurements(runs);
    }

    @Override
    public void initialize() {
        if (currentRun != null) {
            runs.add(new Run(currentRun));
        }
        currentRun = new HashMap<>();
    }

    @Override
    public void monitor(State state) {
        SelfAdaptiveSystemState<?, ?, ?> sasState = (SelfAdaptiveSystemState<?, ?, ?>) state;
        for (SimulatedMeasurementSpecification measurementSpec : measurementSpecs) {
            StateQuantity quantifiedState = sasState.getQuantifiedState();
            SimulatedMeasurement measurement = quantifiedState.findMeasurementWith(measurementSpec)
                .orElseThrow();
            String measurementName = measurementSpec.getName();
            double measuredValue = measurement.getValue();
            LOGGER.info(String.format("measured '%s': %f", measurementName, measuredValue));
            List<Double> measurements = currentRun.get(measurementName);
            if (measurements == null) {
                measurements = new ArrayList<>();
            }
            measurements.add(measuredValue);
            currentRun.put(measurementName, measurements);
        }
    }

}
