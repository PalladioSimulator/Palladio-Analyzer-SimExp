package org.palladiosimulator.simexp.workflow.launcher;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;

public abstract class BaseSimulationExecutorFactory {

    protected IQVToReconfigurationManager createQvtoReconfigurationManager(Experiment experiment) {
        String reconfigurationRulesLocation = getReconfigurationRulesLocation(experiment);
        IQVToReconfigurationManager qvtoReconfigurationManager = new QVToReconfigurationManager(
                reconfigurationRulesLocation);
        return qvtoReconfigurationManager;
    }

    protected String getReconfigurationRulesLocation(Experiment experiment) {
        String path = experiment.getInitialModel()
            .getReconfigurationRules()
            .getFolderUri();
        experiment.getInitialModel()
            .setReconfigurationRules(null);
        return path;
    }

}
