package org.palladiosimulator.simexp.workflow.trafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.pcm.action.IQVTOModelTransformationLoader;
import org.palladiosimulator.simexp.pcm.action.QVTOModelTransformationLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.workflow.launcher.PcmModelLoader;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class TrafoNameProvider implements ITrafoNameProvider {
    private static final Logger LOGGER = Logger.getLogger(TrafoNameProvider.class);

    static {
        LOGGER.addAppender(new ConsoleAppender(new PatternLayout(), ConsoleAppender.SYSTEM_OUT));
        LOGGER.setLevel(Level.DEBUG);
    }

    @Override
    public List<String> getAvailableTransformations(URI experimentsFile) {
        if (experimentsFile.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            LOGGER.debug("new experiment file: " + experimentsFile);
            PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
            ModelLoader modelLoader = modelLoaderFactory.create();
            Experiment experiment = modelLoader.loadExperiment(experimentsFile);
            String reconfigurationRulesLocation = getReconfigurationRulesLocation(experiment);
            LOGGER.debug("rules folder: " + reconfigurationRulesLocation);
            IQVTOModelTransformationLoader qvtoModelTransformationLoader = new QVTOModelTransformationLoader(
                    reconfigurationRulesLocation);
            List<String> availableTransformations = getAvailableTransformations(qvtoModelTransformationLoader);
            LOGGER.debug("found: " + StringUtils.join(availableTransformations, ", "));
            return availableTransformations;
        } catch (Exception e) {
            LOGGER.error("failed to load transformation names", e);
            return Collections.emptyList();
        }
    }

    private List<String> getAvailableTransformations(IQVTOModelTransformationLoader qvtoModelTransformationLoader) {
        List<String> availableTransformations = new ArrayList<>();
        List<QvtoModelTransformation> loadQVTOReconfigurations = qvtoModelTransformationLoader
            .loadQVTOReconfigurations();
        for (QvtoModelTransformation qvto : loadQVTOReconfigurations) {
            String transformationName = qvto.getTransformationName();
            availableTransformations.add(transformationName);
        }
        return availableTransformations;
    }

    private String getReconfigurationRulesLocation(Experiment experiment) {
        String path = experiment.getInitialModel()
            .getReconfigurationRules()
            .getFolderUri();
        return path;
    }
}
