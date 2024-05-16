package org.palladiosimulator.simexp.pcm.action;

import java.util.Collections;

import org.palladiosimulator.simexp.core.action.ReconfigurationImpl;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public abstract class BaseQVToReconfiguration extends ReconfigurationImpl<QVTOReconfigurator> {

    protected static final String EMPTY_RECONFIGURATION_NAME = "EmptyReconf";

    protected boolean executeTransformation(QVTOReconfigurator qvtoReconf, QvtoModelTransformation transformation,
            IResourceTableManager resourceTableManager) {
        boolean succeded = qvtoReconf.runExecute(Collections.singletonList(transformation), null, resourceTableManager);
        return succeded;
    }

    protected abstract boolean isEmptyReconfiguration();

    protected abstract String getTransformationName();

    @Override
    public String getStringRepresentation() {
        if (isEmptyReconfiguration()) {
            return EMPTY_RECONFIGURATION_NAME;
        }
        return getTransformationName();
    }
}