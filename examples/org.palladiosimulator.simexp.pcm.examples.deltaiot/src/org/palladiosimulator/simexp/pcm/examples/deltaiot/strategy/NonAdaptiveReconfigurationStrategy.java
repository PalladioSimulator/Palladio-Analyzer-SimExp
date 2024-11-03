package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSampleLogger;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

public class NonAdaptiveReconfigurationStrategy
        extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

    public NonAdaptiveReconfigurationStrategy(DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        super(new DeltaIoTSampleLogger(modelAccess));
    }

    @Override
    public String getId() {
        return "NonAdaptiveReconfigurationStrategy";
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {

    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        return false;
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        // Nothing to plan
        return emptyReconfiguration();
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return EmptyQVToReconfiguration.empty();
    }

}
