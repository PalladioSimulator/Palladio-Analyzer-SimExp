package org.palladiosimulator.simexp.pcm.performability;

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
import org.palladiosimulator.simexp.core.evaluation.PerformabilityEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
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
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledPerformabilityPcmExperienceSimulationExecutorFactory
        extends ModelledPcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>> {

    public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);

    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";

    public ModelledPerformabilityPcmExperienceSimulationExecutorFactory(
            IModelledPcmWorkflowConfiguration workflowConfiguration, ModelledModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, SimulatedExperienceAccessor accessor, Path resourcePath) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor,
                resourcePath);
    }

    @Override
    protected ModelledSimulationExecutor<Double> doModelledCreate(Experiment experiment,
            ProbabilisticModelRepository probabilisticModelRepository, DynamicBayesianNetwork<CategoricalValue> dbn,
            Smodel smodel) {
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        PerformabilityVaryingInterarrivelRateProcess<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> p = new PerformabilityVaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        p.init(getSeedProvider());
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();
        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new LinkedHashSet<>(pcmMeasurementSpecs);
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator = new InitialPcmStateCreator<>(
                simulatedMeasurementSpecs, experimentProvider, simulationRunnerHolder);

        List<ExperienceSimulationRunner> runners = List
            .of(new PerformabilityPcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        List<Initializable> beforeExecutionInitializables = new ArrayList<>();
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);
        beforeExecutionInitializables.add(beforeExecutionInitializable);

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(pcmMeasurementSpecs);
        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                probabilisticModelRepository);
        IOptimizedConfiguration optimizedConfiguration = getOptimizedConfiguration(getWorkflowConfiguration(), smodel);
        List<OptimizableValue<?>> optimizableValues = optimizedConfiguration.getOptimizableValues();
        OptimizableValueProvider optimizableValueProvider = new OptimizableValueProvider(optimizableValues);
        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider, environmentVariableValueProvider);
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                environmentVariableValueProvider, optimizableValueProvider);
        beforeExecutionInitializables.add(() -> smodelInterpreter.reset());
        String reconfigurationStrategyId = smodel.getModelName();
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(null,
                reconfigurationStrategyId, monitor, smodelInterpreter, smodelInterpreter, qvtoReconfigurationManager);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        Set<QVToReconfiguration> reconfigurations = qvToReconfigurationProvider.getReconfigurations();

        // FIXME: read thresholds from kmodel
        Pair<SimulatedMeasurementSpecification, Threshold> upperThresh = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
        Pair<SimulatedMeasurementSpecification, Threshold> lowerThresh = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
        RewardEvaluator<Double> evaluator = new PerformabilityRewardEvaluation(pcmMeasurementSpecs.get(0),
                pcmMeasurementSpecs.get(1), upperThresh, lowerThresh);

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> experienceSimulator = createExperienceSimulator(
                experiment, pcmMeasurementSpecs, runners, getSimulationParameters(), beforeExecutionInitializables,
                envProcess, getSimulatedExperienceStore(), null, reconfStrategy, reconfigurations, evaluator, false,
                experimentProvider, simulationRunnerHolder, null, getSeedProvider());

        TotalRewardCalculation rewardCalculation = PerformabilityEvaluator.of(getAccessor());

        ModelledSimulationExecutor<Double> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                getSimulationParameters(), reconfStrategy, rewardCalculation, experimentProvider);
        return executor;
    }

}
