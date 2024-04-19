package org.palladiosimulator.simexp.pcm.action;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.ECollections;
import org.palladiosimulator.simexp.pcm.state.IPCMReconfigurationExecutor;
import org.palladiosimulator.simexp.pcm.state.PcmArchitecturalConfiguration;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class QVToReconfiguration extends BaseQVToReconfiguration implements IPCMReconfigurationExecutor {
    private static final Logger LOGGER = Logger.getLogger(PcmArchitecturalConfiguration.class);

    private final QvtoModelTransformation transformation;

    protected QVToReconfiguration(QVToReconfiguration transformation) {
        this(transformation.transformation, transformation.qvtoReconfigurationManager);
    }

    protected QVToReconfiguration(QvtoModelTransformation transformation,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        super(qvtoReconfigurationManager);
        this.transformation = transformation;
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

    @Override
    protected boolean isEmptyReconfiguration() {
        return transformation == null;
    }

    @Override
    protected String getTransformationName() {
        return transformation.getTransformationName();
    }
}
