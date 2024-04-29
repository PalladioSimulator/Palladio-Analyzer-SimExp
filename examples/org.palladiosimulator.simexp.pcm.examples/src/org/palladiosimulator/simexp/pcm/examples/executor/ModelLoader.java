package org.palladiosimulator.simexp.pcm.examples.executor;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;

public interface ModelLoader {
    interface Factory {
        ModelLoader create();
    }

    Experiment loadExperiment(URI experimentsFileURI);

    ProbabilisticModelRepository loadProbabilisticModelRepository(URI probabilisticModelURI);

    DynamicBehaviourRepository loadDynamicBehaviourRepository(URI dynamicModelURI);
}
