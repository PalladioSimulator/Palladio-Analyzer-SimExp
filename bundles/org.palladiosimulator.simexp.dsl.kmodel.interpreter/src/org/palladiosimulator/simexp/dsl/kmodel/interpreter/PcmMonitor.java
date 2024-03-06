package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.strategy.mape.Monitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class PcmMonitor implements Monitor {

    private List<SimulatedMeasurementSpecification> measurementSpecs;
    private ProbeValueProviderMeasurementInjector pvpInjector;

    public PcmMonitor(List<SimulatedMeasurementSpecification> measurementSpecs,
            ProbeValueProviderMeasurementInjector pvpInjector) {
        this.measurementSpecs = measurementSpecs;
        this.pvpInjector = pvpInjector;
    }

    @Override
    public void monitor(State source, SharedKnowledge knowledge) {
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
