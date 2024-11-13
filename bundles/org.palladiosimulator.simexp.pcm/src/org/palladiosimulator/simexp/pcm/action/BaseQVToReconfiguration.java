package org.palladiosimulator.simexp.pcm.action;

import org.palladiosimulator.simexp.core.action.ReconfigurationImpl;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public abstract class BaseQVToReconfiguration extends ReconfigurationImpl<QVTOReconfigurator> {

    protected static final String EMPTY_RECONFIGURATION_NAME = "EmptyReconf";

    protected abstract boolean isEmptyReconfiguration();

    protected abstract String getTransformationName();

    @Override
    public String getReconfigurationName() {
        if (isEmptyReconfiguration()) {
            return EMPTY_RECONFIGURATION_NAME;
        }
        return getTransformationName();
    }
}