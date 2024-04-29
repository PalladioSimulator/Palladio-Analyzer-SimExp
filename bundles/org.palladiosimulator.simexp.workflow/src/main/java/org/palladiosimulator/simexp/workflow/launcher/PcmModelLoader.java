package org.palladiosimulator.simexp.workflow.launcher;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.simexp.model.io.DynamicBehaviourLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryResolver;
import org.palladiosimulator.simexp.model.io.ProbabilisticModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;

public class PcmModelLoader implements ModelLoader {
    private static final Logger LOGGER = Logger.getLogger(PcmModelLoader.class);

    @Override
    public Experiment loadExperiment(ResourceSet rs, URI experimentsFileURI) {
        ExperimentRepositoryLoader expLoader = new ExperimentRepositoryLoader();
        LOGGER.debug(String.format("Loading experiment from: '%s'", experimentsFileURI));
        ExperimentRepository experimentRepository = expLoader.load(rs, experimentsFileURI);

        ExperimentRepositoryResolver expRepoResolver = new ExperimentRepositoryResolver();
        Experiment experiment = expRepoResolver.resolveExperiment(experimentRepository);
        return experiment;
    }

    @Override
    public ProbabilisticModelRepository loadProbabilisticModelRepository(ResourceSet rs, URI probabilisticModelURI) {
        ProbabilisticModelLoader gpnLoader = new ProbabilisticModelLoader();
        LOGGER.debug(String.format("Loading static model from: '%s'", probabilisticModelURI));
        // env model assumption: a ProbabilisticModelRepository (root) contains a single
        // GroundProbabilisticNetwork
        ProbabilisticModelRepository probModelRepo = gpnLoader.load(rs, probabilisticModelURI);
        return probModelRepo;
    }

    @Override
    public DynamicBehaviourRepository loadDynamicBehaviourRepository(ResourceSet rs, URI dynamicModelURI) {
        DynamicBehaviourLoader dbeLoader = new DynamicBehaviourLoader();
        LOGGER.debug(String.format("Loading dynamic model from: '%s'", dynamicModelURI));
        // env model assumption: a DynamicBehaviourRepository (root) contains a single
        // DynamicBehaviourExtension
        DynamicBehaviourRepository dynBehaveRepo = dbeLoader.load(rs, dynamicModelURI);
        return dynBehaveRepo;
    }
}
