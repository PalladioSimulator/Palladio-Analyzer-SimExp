package org.palladiosimulator.simexp.core.quality;

import java.util.ArrayList;
import java.util.List;

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

    public QualityEvaluator(List<? extends SimulatedMeasurementSpecification> measurementSpecs) {
        this.measurementSpecs = new ArrayList<>(measurementSpecs);
    }

    @Override
    public QualityMeasurements getQualityMeasurements() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        LOGGER.info("new run");
    }

    @Override
    public void monitor(State state) {
        SelfAdaptiveSystemState<?, ?, ?> sasState = (SelfAdaptiveSystemState<?, ?, ?>) state;
        for (SimulatedMeasurementSpecification measurementSpec : measurementSpecs) {
            StateQuantity quantifiedState = sasState.getQuantifiedState();
            SimulatedMeasurement measurement = quantifiedState.findMeasurementWith(measurementSpec)
                .orElseThrow();
            double measurementValue = measurement.getValue();

            LOGGER.info(String.format("measured '%s': %f", measurementSpec.getName(), measurementValue));
        }
    }

}
