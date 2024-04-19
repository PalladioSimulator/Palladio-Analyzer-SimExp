package org.palladiosimulator.simexp.pcm.action;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.ECollections;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.pcm.state.IPCMReconfigurationExecutor;
import org.palladiosimulator.simexp.pcm.state.PcmArchitecturalConfiguration;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class QVToReconfiguration extends Reconfiguration<QVTOReconfigurator> implements IPCMReconfigurationExecutor {
    private static final Logger LOGGER = Logger.getLogger(PcmArchitecturalConfiguration.class);
    private static final String EMPTY_RECONFIGURATION_NAME = "EmptyReconf";

    private final QvtoModelTransformation transformation;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    protected QVToReconfiguration(QVToReconfiguration transformation) {
        this(transformation.transformation, transformation.qvtoReconfigurationManager);
    }

    protected QVToReconfiguration(QvtoModelTransformation transformation,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        this.transformation = transformation;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
    }

    public static QVToReconfiguration of(QvtoModelTransformation transformation,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new QVToReconfiguration(transformation, qvtoReconfigurationManager);
    }

    public static QVToReconfiguration empty() {
        return new QVToReconfiguration((QvtoModelTransformation) null, null);
    }

    @Override
    public void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager) {
        if (isEmptyReconfiguration()) {
            return;
        }
        QVTOReconfigurator qvtoReconf = qvtoReconfigurationManager.getReconfigurator(experimentProvider);
        String transformationName = transformation.getTransformationName();
        final boolean succeded = qvtoReconf.runExecute(ECollections.asEList(transformation), null,
                resourceTableManager);
        if (succeded) {
            LOGGER.info(String.format("'EXECUTE' applied reconfiguration '%s'", transformationName));
        } else {
            LOGGER.error(String.format(
                    "'EXECUTE' failed to apply reconfiguration: reconfiguration engine could not execute reconfiguration '%s'",
                    transformationName));
        }
    }

    private boolean isEmptyReconfiguration() {
        return transformation == null;
    }

    @Override
    public String getStringRepresentation() {
        if (isEmptyReconfiguration()) {
            return EMPTY_RECONFIGURATION_NAME;
        }
        return transformation.getTransformationName();
    }

}
