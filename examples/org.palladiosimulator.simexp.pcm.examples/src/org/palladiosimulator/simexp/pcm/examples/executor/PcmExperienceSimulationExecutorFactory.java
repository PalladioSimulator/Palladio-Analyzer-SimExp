package org.palladiosimulator.simexp.pcm.examples.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public abstract class PcmExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification> {
    protected final Experiment experiment;
    protected final DynamicBayesianNetwork<CategoricalValue> dbn;
    protected final List<T> specs;
    protected final SimulationParameters params;
    protected final SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore;

    protected final IProbabilityDistributionFactory<CategoricalValue> distributionFactory;
    protected final IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry;
    protected final ParameterParser parameterParser;
    protected final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
    protected final IExperimentProvider experimentProvider;
    protected final IQVToReconfigurationManager qvtoReconfigurationManager;
    private final SimulationRunnerHolder simulationRunnerHolder;

    public PcmExperienceSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            List<T> specs, SimulationParameters params,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder simulationRunnerHolder) {
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
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
        this.simulationRunnerHolder = simulationRunnerHolder;

        probabilityDistributionRegistry
            .register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
    }

    public abstract PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> create();

    protected ExperienceSimulator<PCMInstance, QVTOReconfigurator, R> createExperienceSimulator(Experiment experiment,
            List<? extends SimulatedMeasurementSpecification> specs, List<ExperienceSimulationRunner> runners,
            SimulationParameters params, Initializable beforeExecution,
            EnvironmentProcess<QVTOReconfigurator, R, V> envProcess,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, V> navigator,
            Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy, Set<QVToReconfiguration> reconfigurations,
            RewardEvaluator<R> evaluator, boolean hidden) {

        return PcmExperienceSimulationBuilder
            .<QVTOReconfigurator, QVToReconfiguration, R, V> newBuilder(experimentProvider, qvtoReconfigurationManager,
                    simulationRunnerHolder)
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
