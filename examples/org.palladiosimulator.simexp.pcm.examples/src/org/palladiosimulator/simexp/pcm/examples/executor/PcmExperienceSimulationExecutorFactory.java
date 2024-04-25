package org.palladiosimulator.simexp.pcm.examples.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
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
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public abstract class PcmExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification> {
    private final IWorkflowConfiguration workflowConfiguration;
    private final Experiment experiment;
    private final DynamicBayesianNetwork<CategoricalValue> dbn;
    private final List<T> specs;
    private final SimulationParameters params;
    private final SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore;
    private final IProbabilityDistributionFactory<CategoricalValue> distributionFactory;
    private final IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry;
    private final ParameterParser parameterParser;
    private final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
    private final IExperimentProvider experimentProvider;
    private final SimulationRunnerHolder simulationRunnerHolder;

    public PcmExperienceSimulationExecutorFactory(IWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn, List<T> specs,
            SimulationParameters params, SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, SimulationRunnerHolder simulationRunnerHolder) {
        this.workflowConfiguration = workflowConfiguration;
        this.experiment = experiment;
        this.dbn = dbn;
        this.specs = specs;
        this.params = params;
        this.simulatedExperienceStore = simulatedExperienceStore;
        this.distributionFactory = distributionFactory;
        this.probabilityDistributionRegistry = probabilityDistributionRegistry;
        this.parameterParser = parameterParser;
        this.probDistRepoLookup = probDistRepoLookup;
        this.experimentProvider = experimentProvider;
        this.simulationRunnerHolder = simulationRunnerHolder;

        probabilityDistributionRegistry
            .register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
    }

    public abstract PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> create();

    protected Experiment getExperiment() {
        return experiment;
    }

    protected IExperimentProvider getExperimentProvider() {
        return experimentProvider;
    }

    protected DynamicBayesianNetwork<CategoricalValue> getDbn() {
        return dbn;
    }

    protected SimulationParameters getSimulationParameters() {
        return params;
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

    protected List<T> getSpecs() {
        return specs;
    }

    protected ExperienceSimulator<PCMInstance, QVTOReconfigurator, R> createExperienceSimulator(Experiment experiment,
            List<? extends SimulatedMeasurementSpecification> specs, List<ExperienceSimulationRunner> runners,
            SimulationParameters params, Initializable beforeExecution,
            EnvironmentProcess<QVTOReconfigurator, R, V> envProcess,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, V> navigator,
            Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy, Set<QVToReconfiguration> reconfigurations,
            RewardEvaluator<R> evaluator, boolean hidden) {

        return PcmExperienceSimulationBuilder
            .<QVTOReconfigurator, QVToReconfiguration, R, V> newBuilder(getExperimentProvider(), simulationRunnerHolder)
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
}
