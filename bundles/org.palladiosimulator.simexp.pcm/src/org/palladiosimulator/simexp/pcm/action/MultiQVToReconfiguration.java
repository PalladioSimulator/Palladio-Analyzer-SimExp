package org.palladiosimulator.simexp.pcm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.ECollections;
import org.palladiosimulator.simexp.pcm.state.IPCMReconfigurationExecutor;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class MultiQVToReconfiguration extends BaseQVToReconfiguration implements IPCMReconfigurationExecutor {
    private static final Logger LOGGER = Logger.getLogger(MultiQVToReconfiguration.class);

    private final List<QvtoModelTransformation> transformations;

    private MultiQVToReconfiguration(List<QvtoModelTransformation> transformations,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        super(qvtoReconfigurationManager);
        this.transformations = Collections.unmodifiableList(transformations);
    }

    public static MultiQVToReconfiguration of(List<QvtoModelTransformation> transformations,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new MultiQVToReconfiguration(transformations, qvtoReconfigurationManager);
    }

    @Override
    public void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager) {
        LOGGER.info(String.format("'EXECUTE' applying %d reconfigurations", transformations.size()));
        QVTOReconfigurator qvtoReconf = qvtoReconfigurationManager.getReconfigurator(experimentProvider);
        ListIterator<QvtoModelTransformation> trafoIterator = transformations.listIterator();
        while (trafoIterator.hasNext()) {
            QvtoModelTransformation transformation = trafoIterator.next();
            int index = trafoIterator.nextIndex();
            String transformationName = transformation.getTransformationName();
            boolean succeded = qvtoReconf.runExecute(ECollections.asEList(transformation), null, resourceTableManager);
            if (succeded) {
                LOGGER.info(String.format("'EXECUTE' applied reconfiguration %d: '%s'", index, transformationName));
            } else {
                LOGGER.error(String.format(
                        "'EXECUTE' failed to apply reconfiguration: reconfiguration engine could not execute reconfiguration %d: '%s'",
                        index, transformationName));
                break;
            }
        }
    }

    @Override
    protected boolean isEmptyReconfiguration() {
        return transformations.isEmpty();
    }

    @Override
    protected String getTransformationName() {
        List<String> names = new ArrayList<>();
        for (QvtoModelTransformation trafo : transformations) {
            names.add(trafo.getTransformationName());
        }
        return StringUtils.join(names, ",");
    }
}
