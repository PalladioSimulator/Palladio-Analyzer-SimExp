package org.palladiosimulator.simexp.pcm.action;

import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class EmptyQVToReconfiguration extends BaseQVToReconfiguration implements QVToReconfiguration {

    @Override
    public void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager) {
    }

    @Override
    protected boolean isEmptyReconfiguration() {
        return true;
    }

    @Override
    protected String getTransformationName() {
        return null;
    }

    public static QVToReconfiguration empty() {
        return new EmptyQVToReconfiguration();
    }
}
