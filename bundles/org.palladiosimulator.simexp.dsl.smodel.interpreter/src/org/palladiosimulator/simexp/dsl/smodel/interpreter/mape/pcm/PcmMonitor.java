package org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.pcm;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.pcm.ProbeValueProviderMeasurementInjector;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class PcmMonitor implements Monitor {

    private final List<SimulatedMeasurementSpecification> measurementSpecs;
    private final ProbeValueProviderMeasurementInjector pvpInjector;

    public PcmMonitor(List<SimulatedMeasurementSpecification> measurementSpecs,
            ProbeValueProviderMeasurementInjector pvpInjector) {
        this.measurementSpecs = measurementSpecs;
        this.pvpInjector = pvpInjector;
    }

    @Override
    public void monitor(State source) {
        SelfAdaptiveSystemState<PCMInstance, QVTOReconfigurator, List<InputValue<CategoricalValue>>> sasState = (SelfAdaptiveSystemState<PCMInstance, QVTOReconfigurator, List<InputValue<CategoricalValue>>>) source;

        for (SimulatedMeasurementSpecification measurementSpec : measurementSpecs) {
            SimulatedMeasurement measurement = sasState.getQuantifiedState()
                .findMeasurementWith(measurementSpec)
                .orElseThrow();
            double currentMeasurementValue = measurement.getValue();
            pvpInjector.injectMeasurement(measurementSpec, currentMeasurementValue);
        }

    }

}
