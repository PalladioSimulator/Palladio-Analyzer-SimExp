package org.palladiosimulator.simexp.pcm.action;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class SingleQVToReconfiguration extends BaseQVToReconfiguration implements QVToReconfiguration {
    private static final Logger LOGGER = Logger.getLogger(SingleQVToReconfiguration.class);

    private final QvtoModelTransformation transformation;

    protected SingleQVToReconfiguration(SingleQVToReconfiguration transformation) {
        this(transformation.transformation, transformation.qvtoReconfigurationManager);
    }

    protected SingleQVToReconfiguration(QvtoModelTransformation transformation,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        super(qvtoReconfigurationManager);
        this.transformation = transformation;
    }

    public static SingleQVToReconfiguration of(QvtoModelTransformation transformation,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new SingleQVToReconfiguration(transformation, qvtoReconfigurationManager);
    }

    @Override
    public void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager) {
        if (isEmptyReconfiguration()) {
            return;
        }
        QVTOReconfigurator qvtoReconf = qvtoReconfigurationManager.getReconfigurator(experimentProvider);
        String transformationName = transformation.getTransformationName();
        boolean succeded = executeTransformation(qvtoReconf, transformation, resourceTableManager);
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
