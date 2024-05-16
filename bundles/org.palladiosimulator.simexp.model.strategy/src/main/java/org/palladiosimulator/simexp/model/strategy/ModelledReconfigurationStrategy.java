package org.palladiosimulator.simexp.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.MultiQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class ModelledReconfigurationStrategy extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

    private final Monitor monitor;
    private final Analyzer analyzer;
    private final Planner planner;
    private final String strategyId;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    public ModelledReconfigurationStrategy(String strategyId, Monitor monitor, Analyzer analyzer, Planner planner,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        this.strategyId = strategyId;
        this.monitor = monitor;
        this.analyzer = analyzer;
        this.planner = planner;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
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
        QVToReconfiguration plannedReconfiguration = createReconfiguration(resolvedActions);
        return plannedReconfiguration;
    }

    /**
     * 
     * implements lookup between QVToReconfiguration and actions retrieved from planning phase
     * 
     */
    private QVToReconfiguration createReconfiguration(List<ResolvedAction> actions) {
        List<SingleQVToReconfiguration> reconfigurations = new ArrayList<>();
        for (ResolvedAction resolvedAction : actions) {
            String resolvedActionName = resolvedAction.getAction()
                .getName();
            QvtoModelTransformation transformation = qvtoReconfigurationManager
                .findQvtoModelTransformation(resolvedActionName);

            SingleQVToReconfiguration reconfiguration = SingleQVToReconfiguration.of(transformation,
                    qvtoReconfigurationManager);
            reconfigurations.add(reconfiguration);
        }

        MultiQVToReconfiguration reconfiguration = MultiQVToReconfiguration.of(reconfigurations);
        return reconfiguration;
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return EmptyQVToReconfiguration.empty();
    }

}
