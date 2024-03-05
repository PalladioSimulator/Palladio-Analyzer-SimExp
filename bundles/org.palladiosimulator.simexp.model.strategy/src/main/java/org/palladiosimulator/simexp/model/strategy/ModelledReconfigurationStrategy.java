package org.palladiosimulator.simexp.model.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.strategy.mape.Analyzer;
import org.palladiosimulator.simexp.core.strategy.mape.Planner;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProviderMeasurementInjector;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class ModelledReconfigurationStrategy extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

//    private final Monitor monitor;
    private final Analyzer analyzer;
    private final Planner planner;
    private final List<SimulatedMeasurementSpecification> measurementSpecs;
    private final ProbeValueProviderMeasurementInjector pvpInjector;

//    private final Kmodel kmodel;

//    KmodelInterpreter(Kmodel model, VariableValueProvider vvp, ProbeValueProvider pvp, RuntimeValueProvider rvp)

    public ModelledReconfigurationStrategy(Analyzer analyzer, Planner planner,
            List<SimulatedMeasurementSpecification> measurementSpecs,
            ProbeValueProviderMeasurementInjector pvpInjector) {
//        this.monitor = monitor;
        this.analyzer = analyzer;
        this.planner = planner;
//        this.executer = executer;
        this.measurementSpecs = measurementSpecs;
        this.pvpInjector = pvpInjector;
    }

    @Override
    public String getId() {
        return "ModelledReconfigurationStrategy";
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        SelfAdaptiveSystemState<PCMInstance, QVTOReconfigurator, List<InputValue<CategoricalValue>>> sasState = (SelfAdaptiveSystemState<PCMInstance, QVTOReconfigurator, List<InputValue<CategoricalValue>>>) source;

        for (SimulatedMeasurementSpecification measurementSpec : measurementSpecs) {
            SimulatedMeasurement measurement = sasState.getQuantifiedState()
                .findMeasurementWith(measurementSpec)
                .orElseThrow();
            double currentMeasurementValue = measurement.getValue();
            pvpInjector.injectMeasurement(measurementSpec, currentMeasurementValue);
        }

    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        return analyzer.analyze();
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        List<ResolvedAction> resolvedActions = planner.plan();
        QVToReconfiguration plannedAction = findReconfiguration(options, resolvedActions);
        return plannedAction;
    }

    /**
     * 
     * implements lookup between QVToReconfiguration and actions retrieved from planning phase
     * 
     */
    private QVToReconfiguration findReconfiguration(Set<QVToReconfiguration> options, List<ResolvedAction> actions) {
        Map<String, QVToReconfiguration> reconfigurationMap = new HashMap<>();
        for (QVToReconfiguration option : options) {
            reconfigurationMap.put(option.getStringRepresentation(), option);
        }
        String resolvedActionName = actions.get(0)
            .getAction()
            .getName();
        QVToReconfiguration reconfiguration = reconfigurationMap.get(resolvedActionName);
        LOGGER.info(String.format("'PLANNING' selected action '%s'", reconfiguration.getStringRepresentation()));
        return reconfiguration;

    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return QVToReconfiguration.empty();
    }

}
