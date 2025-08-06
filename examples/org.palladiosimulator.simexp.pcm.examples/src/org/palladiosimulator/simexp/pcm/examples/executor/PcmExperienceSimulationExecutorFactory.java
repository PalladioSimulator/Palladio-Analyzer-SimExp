package org.palladiosimulator.simexp.pcm.examples.executor;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.commons.constants.model.RewardType;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.quality.QualityEvaluator;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.SimulatedRewardReceiver;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.StateQuantityMonitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.config.ITransformationConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.apache.util.ProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.api.parser.DefaultParameterParser;
import tools.mdsd.probdist.api.parser.ParameterParser;
import tools.mdsd.probdist.api.random.ISeedProvider;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionRepository;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public abstract class PcmExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification> {
    private final IWorkflowConfiguration workflowConfiguration;
    private final ModelLoader.Factory modelLoaderFactory;
    private final ISimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore;
    private final IProbabilityDistributionFactory<CategoricalValue> distributionFactory;
    private final IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry;
    private final ParameterParser parameterParser;
    private final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
    private final Optional<ISeedProvider> seedProvider;
    private final ISimulatedExperienceAccessor accessor;
    private final Path resourcePath;

    public PcmExperienceSimulationExecutorFactory(IWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            ISimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        this.workflowConfiguration = workflowConfiguration;
        this.modelLoaderFactory = modelLoaderFactory;
        this.simulatedExperienceStore = simulatedExperienceStore;
        this.parameterParser = new DefaultParameterParser();
        this.seedProvider = seedProvider;
        this.accessor = accessor;
        this.resourcePath = resourcePath;

        ProbabilityDistributionFactory defaultProbabilityDistributionFactory = new ProbabilityDistributionFactory(
                seedProvider);
        this.probabilityDistributionRegistry = defaultProbabilityDistributionFactory;
        this.distributionFactory = defaultProbabilityDistributionFactory;

        ProbabilityDistributionRepository probabilityDistributionRepository = BasicDistributionTypesLoader
            .loadRepository();
        this.probDistRepoLookup = new ProbabilityDistributionRepositoryLookup(probabilityDistributionRepository);
    }

    protected Path getResourcePath() {
        return resourcePath;
    }

    protected ISimulatedExperienceAccessor getAccessor() {
        return accessor;
    }

    protected TotalRewardCalculation createRewardCalculation(String policyId) {
        RewardType rewardType = workflowConfiguration.getRewardType();
        String simulationID = getSimulationParameters().getSimulationID();
        switch (rewardType) {
        case EXPECTED:
            return new ExpectedRewardEvaluator(getAccessor());
        case ACCUMULATED:
            String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationID, policyId);
            return SimulatedExperienceEvaluator.of(getAccessor(), simulationID, sampleSpaceId);
        }
        throw new RuntimeException("unknown reward type: " + rewardType);
    }

    protected StateQuantityMonitor createStateQuantityMonitor() {
        return new StateQuantityMonitor() {

            @Override
            public void monitor(State state) {
            }
        };
    }

    protected IQualityEvaluator createQualityEvaluator(
            List<? extends SimulatedMeasurementSpecification> measurementSpecs) {
        return new QualityEvaluator(measurementSpecs);
    }

    protected Optional<ISeedProvider> getSeedProvider() {
        return seedProvider;
    }

    public SimulationExecutor create() {
        probabilityDistributionRegistry
            .register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));

        ModelLoader modelLoader = modelLoaderFactory.create();

        URI experimentsFileURI = getWorkflowConfiguration().getExperimentsURI();
        Experiment experiment = modelLoader.loadExperiment(experimentsFileURI);

        URI staticModelURI = getWorkflowConfiguration().getStaticModelURI();
        ProbabilisticModelRepository probabilisticModelRepository = modelLoader
            .loadProbabilisticModelRepository(staticModelURI);

        URI dynamicModelURI = getWorkflowConfiguration().getDynamicModelURI();
        DynamicBehaviourRepository dynamicBehaviourRepository = modelLoader
            .loadDynamicBehaviourRepository(dynamicModelURI);

        return createLoaded(modelLoader, experiment, probabilisticModelRepository, dynamicBehaviourRepository);
    }

    protected SimulationExecutor createLoaded(ModelLoader modelLoader, Experiment experiment,
            ProbabilisticModelRepository probabilisticModelRepository,
            DynamicBehaviourRepository dynamicBehaviourRepository) {
        DynamicBayesianNetwork<CategoricalValue> dbn = createDBN(probabilisticModelRepository,
                dynamicBehaviourRepository);
        return doCreate(experiment, dbn);
    }

    protected DynamicBayesianNetwork<CategoricalValue> createDBN(
            ProbabilisticModelRepository probabilisticModelRepository,
            DynamicBehaviourRepository dynamicBehaviourRepository) {
        GroundProbabilisticNetwork gpn = probabilisticModelRepository.getModels()
            .get(0);
        BayesianNetwork<CategoricalValue> bn = new BayesianNetwork<>(null, gpn, distributionFactory);
        DynamicBehaviourExtension dbe = dynamicBehaviourRepository.getExtensions()
            .get(0);
        DynamicBayesianNetwork<CategoricalValue> dbn = new DynamicBayesianNetwork<>(null, bn, dbe, distributionFactory);
        dbn.init(seedProvider);
        return dbn;
    }

    protected abstract PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> doCreate(
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn);

    protected IWorkflowConfiguration getWorkflowConfiguration() {
        return workflowConfiguration;
    }

    protected IExperimentProvider createExperimentProvider(Experiment experiment) {
        return new ExperimentProvider(experiment);
    }

    protected SimulationParameters getSimulationParameters() {
        return getWorkflowConfiguration().getSimulationParameters();
    }

    protected ParameterParser getParameterParser() {
        return parameterParser;
    }

    protected ISimulatedExperienceStore<QVTOReconfigurator, R> getSimulatedExperienceStore() {
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
            SimulationParameters params, List<Initializable> beforeExecutionInitializables,
            EnvironmentProcess<QVTOReconfigurator, R, V> envProcess,
            ISimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, V> navigator,
            Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy, Set<QVToReconfiguration> reconfigurations,
            RewardEvaluator<R> evaluator, StateQuantityMonitor stateQuantityMonitor, boolean hidden,
            IExperimentProvider experimentProvider, SimulationRunnerHolder simulationRunnerHolder,
            SampleDumper sampleDumper, Optional<ISeedProvider> seedProvider) {
        SimulatedRewardReceiver<PCMInstance, QVTOReconfigurator, R, V> rewardReceiver = SimulatedRewardReceiver
            .<PCMInstance, QVTOReconfigurator, R, V> with(evaluator);

        return PcmExperienceSimulationBuilder
            .<QVTOReconfigurator, QVToReconfiguration, R, V> newBuilder(experimentProvider, simulationRunnerHolder)
            .makeGlobalPcmSettings()
            .withInitialExperiment(experiment)
            .andSimulatedMeasurementSpecs(new LinkedHashSet<>(specs))
            .addExperienceSimulationRunners(new LinkedHashSet<>(runners))
            .done()
            .createSimulationConfiguration()
            .withSimulationID(params.getSimulationID())
            .withNumberOfRuns(params.getNumberOfRuns())
            .withSeedProvider(seedProvider)
            .usingSampleDumper(sampleDumper)
            .andNumberOfSimulationsPerRun(params.getNumberOfSimulationsPerRun())
            .andOptionalExecutionBeforeEachRun(beforeExecutionInitializables)
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
            .withRewardReceiver(rewardReceiver)
            .withStateQuantityMonitor(stateQuantityMonitor)
            .build();
    }

    protected IQVToReconfigurationManager createQvtoReconfigurationManager(Experiment experiment,
            ITransformationConfiguration transformationConfiguration) {
        String reconfigurationRulesLocation = getReconfigurationRulesLocation(experiment);
        IQVToReconfigurationManager qvtoReconfigurationManager = new QVToReconfigurationManager(
                reconfigurationRulesLocation, transformationConfiguration);
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
