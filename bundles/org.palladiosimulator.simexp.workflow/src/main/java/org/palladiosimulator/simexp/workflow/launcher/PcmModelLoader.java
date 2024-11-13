package org.palladiosimulator.simexp.workflow.launcher;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.model.io.DynamicBehaviourLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryResolver;
import org.palladiosimulator.simexp.model.io.ProbabilisticModelLoader;
import org.palladiosimulator.simexp.model.io.SModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;

public class PcmModelLoader implements ModelledModelLoader {
    private static final Logger LOGGER = Logger.getLogger(PcmModelLoader.class);

    private final ResourceSet rs;

    private PcmModelLoader(ResourceSet rs) {
        this.rs = rs;
    }

    public static class Factory implements ModelledModelLoader.Factory {

        @Override
        public ModelledModelLoader create() {
            ResourceSet rs = new ResourceSetImpl();
            return new PcmModelLoader(rs);
        }
    }

    @Override
    public Experiment loadExperiment(URI experimentsFileURI) {
        ExperimentRepositoryLoader expLoader = new ExperimentRepositoryLoader();
        LOGGER.debug(String.format("Loading experiment from: '%s'", experimentsFileURI));
        ExperimentRepository experimentRepository = expLoader.load(rs, experimentsFileURI);

        ExperimentRepositoryResolver expRepoResolver = new ExperimentRepositoryResolver();
        Experiment experiment = expRepoResolver.resolveExperiment(experimentRepository);
        return experiment;
    }

    @Override
    public ProbabilisticModelRepository loadProbabilisticModelRepository(URI probabilisticModelURI) {
        ProbabilisticModelLoader gpnLoader = new ProbabilisticModelLoader();
        LOGGER.debug(String.format("Loading static model from: '%s'", probabilisticModelURI));
        // env model assumption: a ProbabilisticModelRepository (root) contains a single
        // GroundProbabilisticNetwork
        ProbabilisticModelRepository probModelRepo = gpnLoader.load(rs, probabilisticModelURI);
        return probModelRepo;
    }

    @Override
    public DynamicBehaviourRepository loadDynamicBehaviourRepository(URI dynamicModelURI) {
        DynamicBehaviourLoader dbeLoader = new DynamicBehaviourLoader();
        LOGGER.debug(String.format("Loading dynamic model from: '%s'", dynamicModelURI));
        // env model assumption: a DynamicBehaviourRepository (root) contains a single
        // DynamicBehaviourExtension
        DynamicBehaviourRepository dynBehaveRepo = dbeLoader.load(rs, dynamicModelURI);
        return dynBehaveRepo;
    }

    @Override
    public Smodel loadSModel(URI smodelURI) {
        SModelLoader smodelLoader = new SModelLoader();
        LOGGER.debug(String.format("Loading smodel from '%s'", smodelURI.path()));
        Smodel smodel = smodelLoader.load(rs, smodelURI);
        return smodel;
    }
}
