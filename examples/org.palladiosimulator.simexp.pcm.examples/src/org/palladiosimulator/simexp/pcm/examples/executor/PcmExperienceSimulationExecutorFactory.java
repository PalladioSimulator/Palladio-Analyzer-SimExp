package org.palladiosimulator.simexp.pcm.examples.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.io.DynamicBehaviourLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryResolver;
import org.palladiosimulator.simexp.model.io.ProbabilisticModelLoader;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.apache.util.ProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.api.parser.DefaultParameterParser;
import tools.mdsd.probdist.api.parser.ParameterParser;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionRepository;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public abstract class PcmExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification> {
    private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationExecutorFactory.class);

    private final IWorkflowConfiguration workflowConfiguration;
    private final ResourceSet rs;
    private final DynamicBayesianNetwork<CategoricalValue> dbn;
    private final SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore;
    private final IProbabilityDistributionFactory<CategoricalValue> distributionFactory;
    private final IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry;
    private final ParameterParser parameterParser;
    private final IProbabilityDistributionRepositoryLookup probDistRepoLookup;

    protected final ProbabilisticModelRepository probabilisticModelRepository;

    public PcmExperienceSimulationExecutorFactory(IWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore) {
        this.workflowConfiguration = workflowConfiguration;
        this.rs = rs;
        this.simulatedExperienceStore = simulatedExperienceStore;
        this.parameterParser = new DefaultParameterParser();

        ProbabilityDistributionFactory defaultProbabilityDistributionFactory = new ProbabilityDistributionFactory();
        this.probabilityDistributionRegistry = defaultProbabilityDistributionFactory;
        this.distributionFactory = defaultProbabilityDistributionFactory;

        ProbabilityDistributionRepository probabilityDistributionRepository = BasicDistributionTypesLoader
            .loadRepository();
        this.probDistRepoLookup = new ProbabilityDistributionRepositoryLookup(probabilityDistributionRepository);

        this.probabilisticModelRepository = loadProbabilisticModelRepository();
        GroundProbabilisticNetwork gpn = probabilisticModelRepository.getModels()
            .get(0);
        BayesianNetwork<CategoricalValue> bn = new BayesianNetwork<>(null, gpn, distributionFactory);
        DynamicBehaviourRepository dynamicBehaviourRepository = loadDynamicBehaviourRepository();
        DynamicBehaviourExtension dbe = dynamicBehaviourRepository.getExtensions()
            .get(0);
        this.dbn = new DynamicBayesianNetwork<>(null, bn, dbe, distributionFactory);

        probabilityDistributionRegistry
            .register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
    }

    private ProbabilisticModelRepository loadProbabilisticModelRepository() {
        URI staticModelURI = getWorkflowConfiguration().getStaticModelURI();
        ProbabilisticModelLoader gpnLoader = new ProbabilisticModelLoader();
        LOGGER.debug(String.format("Loading static model from: '%s'", staticModelURI));
        // env model assumption: a ProbabilisticModelRepository (root) contains a single
        // GroundProbabilisticNetwork
        ProbabilisticModelRepository probModelRepo = gpnLoader.load(rs, staticModelURI);
        return probModelRepo;
    }

    private DynamicBehaviourRepository loadDynamicBehaviourRepository() {
        URI dynamicModelURI = getWorkflowConfiguration().getDynamicModelURI();
        DynamicBehaviourLoader dbeLoader = new DynamicBehaviourLoader();
        LOGGER.debug(String.format("Loading dynamic model from: '%s'", dynamicModelURI));
        // env model assumption: a DynamicBehaviourRepository (root) contains a single
        // DynamicBehaviourExtension
        DynamicBehaviourRepository dynBehaveRepo = dbeLoader.load(rs, dynamicModelURI);
        return dynBehaveRepo;
    }

    public PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> create() {
        return doCreate();
    }

    protected abstract PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> doCreate();

    protected IWorkflowConfiguration getWorkflowConfiguration() {
        return workflowConfiguration;
    }

    protected Experiment loadExperiment() {
        URI experimentsFileURI = getWorkflowConfiguration().getExperimentsURI();
        ExperimentRepositoryLoader expLoader = new ExperimentRepositoryLoader();
        LOGGER.debug(String.format("Loading experiment from: '%s'", experimentsFileURI));
        ExperimentRepository experimentRepository = expLoader.load(rs, experimentsFileURI);

        ExperimentRepositoryResolver expRepoResolver = new ExperimentRepositoryResolver();
        Experiment experiment = expRepoResolver.resolveExperiment(experimentRepository);
        return experiment;
    }

    protected IExperimentProvider createExperimentProvider(Experiment experiment) {
        return new ExperimentProvider(experiment);
    }

    protected DynamicBayesianNetwork<CategoricalValue> getDbn() {
        return dbn;
    }

    protected SimulationParameters getSimulationParameters() {
        return getWorkflowConfiguration().getSimulationParameters();
    }

    protected ParameterParser getParameterParser() {
        return parameterParser;
    }

    protected SimulatedExperienceStore<QVTOReconfigurator, R> getSimulatedExperienceStore() {
        return simulatedExperienceStore;
    }

    protected IProbabilityDistributionFactory<CategoricalValue> getDistributionFactory() {
        return distributionFactory;
    }

    protected IProbabilityDistributionRegistry<CategoricalValue> getProbabilityDistributionRegistry() {
        return probabilityDistributionRegistry;
    }

    protected IProbabilityDistributionRepositoryLookup getProbDistRepoLookup() {
        return probDistRepoLookup;
    }

    protected abstract List<T> createSpecs(Experiment experiment);

    protected SimulationRunnerHolder createSimulationRunnerHolder() {
        return new SimulationRunnerHolder();
    }

    protected ExperienceSimulator<PCMInstance, QVTOReconfigurator, R> createExperienceSimulator(Experiment experiment,
            List<? extends SimulatedMeasurementSpecification> specs, List<ExperienceSimulationRunner> runners,
            SimulationParameters params, Initializable beforeExecution,
            EnvironmentProcess<QVTOReconfigurator, R, V> envProcess,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, V> navigator,
            Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy, Set<QVToReconfiguration> reconfigurations,
            RewardEvaluator<R> evaluator, boolean hidden, IExperimentProvider experimentProvider,
            SimulationRunnerHolder simulationRunnerHolder) {

        return PcmExperienceSimulationBuilder
            .<QVTOReconfigurator, QVToReconfiguration, R, V> newBuilder(experimentProvider, simulationRunnerHolder)
            .makeGlobalPcmSettings()
            .withInitialExperiment(experiment)
            .andSimulatedMeasurementSpecs(new HashSet<>(specs))
            .addExperienceSimulationRunners(new HashSet<>(runners))
            .done()
            .createSimulationConfiguration()
            .withSimulationID(params.getSimulationID())
            .withNumberOfRuns(params.getNumberOfRuns())
            .andNumberOfSimulationsPerRun(params.getNumberOfSimulationsPerRun())
            .andOptionalExecutionBeforeEachRun(beforeExecution)
            .done()
            .specifySelfAdaptiveSystemState()
            .asEnvironmentalDrivenProcess(envProcess, simulatedExperienceStore, simulationRunnerHolder)
            .asPartiallyEnvironmentalDrivenProcess(navigator)
            .asHiddenProcess(hidden)
            .done()
            .createReconfigurationSpace()
            .addReconfigurations(reconfigurations)
            .andReconfigurationStrategy(reconfStrategy)
            .done()
            .specifyRewardHandling()
            .withRewardEvaluator(evaluator)
            .done()
            .build();
    }

    protected IQVToReconfigurationManager createQvtoReconfigurationManager(Experiment experiment) {
        String reconfigurationRulesLocation = getReconfigurationRulesLocation(experiment);
        IQVToReconfigurationManager qvtoReconfigurationManager = new QVToReconfigurationManager(
                reconfigurationRulesLocation);
        return qvtoReconfigurationManager;
    }

    private String getReconfigurationRulesLocation(Experiment experiment) {
        String path = experiment.getInitialModel()
            .getReconfigurationRules()
            .getFolderUri();
        experiment.getInitialModel()
            .setReconfigurationRules(null);
        return path;
    }

}
