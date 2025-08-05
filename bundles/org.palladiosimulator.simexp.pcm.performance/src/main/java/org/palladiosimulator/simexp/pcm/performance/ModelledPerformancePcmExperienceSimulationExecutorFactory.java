package org.palladiosimulator.simexp.pcm.performance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.mape.PcmMonitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.EnvironmentVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.OptimizableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.config.IOptimizedConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.simulator.ModelledPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledPerformancePcmExperienceSimulationExecutorFactory
        extends ModelledPcmExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>> {

    private final static double UPPER_THRESHOLD_RT = 2.0;
    private final static double LOWER_THRESHOLD_RT = 0.3;

    public ModelledPerformancePcmExperienceSimulationExecutorFactory(
            IModelledPcmWorkflowConfiguration workflowConfiguration, ModelledModelLoader.Factory modelLoaderFactory,
            ISimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor,
                resourcePath);
    }

    @Override
    protected ModelledSimulationExecutor<Integer> doModelledCreate(Experiment experiment,
            ProbabilisticModelRepository probabilisticModelRepository, DynamicBayesianNetwork<CategoricalValue> dbn,
            Smodel smodel) {
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        PerformanceVaryingInterarrivelRateProcess<QVTOReconfigurator, QVToReconfiguration, Integer> p = new PerformanceVaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        p.init(getSeedProvider());
        EnvironmentProcess<QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();
        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new LinkedHashSet<>(pcmMeasurementSpecs);
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator = new InitialPcmStateCreator<>(
                simulatedMeasurementSpecs, experimentProvider, simulationRunnerHolder);

        List<ExperienceSimulationRunner> runners = List
            .of(new PcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        List<Initializable> beforeExecutionInitializables = new ArrayList<>();
        Initializable beforeExecution = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);
        beforeExecutionInitializables.add(beforeExecution);

        // Monitor PcmMonitor
        // Analyzer SmodelInterpreter
        // Planner SmodelInterpreter
        // Executor not required

        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                probabilisticModelRepository);
        EnvironmentVariableValueProvider envVariableValueProvider = new EnvironmentVariableValueProvider(
                probabilisticModelRepository);
        IOptimizedConfiguration optimizedConfiguration = getOptimizedConfiguration(getWorkflowConfiguration(), smodel);
        List<OptimizableValue<?>> optimizableValues = optimizedConfiguration.getOptimizableValues();
        OptimizableValueProvider optimizableValueProvider = new OptimizableValueProvider(optimizableValues);
        Monitor monitor = new PcmMonitor(pcmMeasurementSpecs, probeValueProvider, environmentVariableValueProvider);
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                envVariableValueProvider, optimizableValueProvider);
        beforeExecutionInitializables.add(() -> smodelInterpreter.reset());
        String reconfigurationStrategyId = smodel.getModelName();
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(null,
                reconfigurationStrategyId, monitor, smodelInterpreter, smodelInterpreter, qvtoReconfigurationManager);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        Set<QVToReconfiguration> reconfigurations = qvToReconfigurationProvider.getReconfigurations();

        // FIXME: read thresholds from smodel
        Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(threshold);
        boolean isHidden = false;

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> experienceSimulator = createExperienceSimulator(
                experiment, pcmMeasurementSpecs, runners, getSimulationParameters(), beforeExecutionInitializables,
                envProcess, getSimulatedExperienceStore(), null, reconfStrategy, reconfigurations, evaluator, isHidden,
                experimentProvider, simulationRunnerHolder, null, getSeedProvider());

        TotalRewardCalculation rewardCalculation = createRewardCalculation(reconfStrategy.getId());
        IQualityEvaluator qualityEvaluator = createQualityEvaluator(pcmMeasurementSpecs);

        ModelledSimulationExecutor<Integer> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                getSimulationParameters(), reconfStrategy, rewardCalculation, qualityEvaluator, experimentProvider);
        return executor;
    }

}
