package org.palladiosimulator.simexp.pcm.examples.executor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;

public interface ModelLoader {
    Experiment loadExperiment(ResourceSet rs, URI experimentsFileURI);

    ProbabilisticModelRepository loadProbabilisticModelRepository(ResourceSet rs, URI probabilisticModelURI);

    DynamicBehaviourRepository loadDynamicBehaviourRepository(ResourceSet rs, URI dynamicModelURI);
}
