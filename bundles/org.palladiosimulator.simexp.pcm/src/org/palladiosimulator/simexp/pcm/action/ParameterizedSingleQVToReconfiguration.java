package org.palladiosimulator.simexp.pcm.action;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class ParameterizedSingleQVToReconfiguration extends SingleQVToReconfiguration {
    private final Map<String, Object> parameters;

    public ParameterizedSingleQVToReconfiguration(QvtoModelTransformation transformation,
            IQVToReconfigurationManager qvtoReconfigurationManager, Map<String, Object> parameters) {
        super(transformation, qvtoReconfigurationManager);
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    @Override
    protected boolean executeTransformation(QVTOReconfigurator qvtoReconf, QvtoModelTransformation transformation,
            IResourceTableManager resourceTableManager) {
        List<QvtoModelTransformation> actions = Collections.singletonList(transformation);
        boolean succeded = qvtoReconf.runExecute(actions, null, resourceTableManager, parameters);
        return succeded;
    }
}
