package org.palladiosimulator.simexp.model.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class ModelledReconfigurationStrategy extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

    private final Monitor monitor;
    private final Analyzer analyzer;
    private final Planner planner;
    private final String strategyId;

    public ModelledReconfigurationStrategy(String strategyId, Monitor monitor, Analyzer analyzer, Planner planner) {
        this.strategyId = strategyId;
        this.monitor = monitor;
        this.analyzer = analyzer;
        this.planner = planner;
    }

    @Override
    public String getId() {
        return strategyId;
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        monitor.monitor(source);

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
        return SingleQVToReconfiguration.empty();
    }

}
