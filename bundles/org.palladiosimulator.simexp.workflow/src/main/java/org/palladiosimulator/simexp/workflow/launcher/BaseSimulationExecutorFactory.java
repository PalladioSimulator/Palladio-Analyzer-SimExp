package org.palladiosimulator.simexp.workflow.launcher;

import org.palladiosimulator.experimentautomation.experiments.Experiment;

public abstract class BaseSimulationExecutorFactory {

    protected String getReconfigurationRulesLocation(Experiment experiment) {
        String path = experiment.getInitialModel()
            .getReconfigurationRules()
            .getFolderUri();
        experiment.getInitialModel()
            .setReconfigurationRules(null);
        return path;
    }

}
