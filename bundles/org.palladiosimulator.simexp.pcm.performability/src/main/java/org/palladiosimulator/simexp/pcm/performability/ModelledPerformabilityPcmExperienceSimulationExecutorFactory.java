package org.palladiosimulator.simexp.pcm.performability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;
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
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.mape.PcmMonitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.EnvironmentVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.modelled.simulator.ModelledPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class ModelledPerformabilityPcmExperienceSimulationExecutorFactory
        extends ModelledPcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>> {

    public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);

    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";

    private final ProbabilisticModelRepository staticEnvDynModel;

    public ModelledPerformabilityPcmExperienceSimulationExecutorFactory(
            IModelledPcmWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore) {
        super(workflowConfiguration, rs, simulatedExperienceStore);
        this.staticEnvDynModel = probabilisticModelRepository;
    }

    @Override
    public ModelledSimulationExecutor<Double> create() {
        Experiment experiment = loadExperiment();
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        PerformabilityVaryingInterarrivelRateProcess<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> p = new PerformabilityVaryingInterarrivelRateProcess<>(
                getDbn(), experimentProvider);
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();
        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new HashSet<>(pcmMeasurementSpecs);
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator = new InitialPcmStateCreator<>(
                simulatedMeasurementSpecs, experimentProvider, simulationRunnerHolder);

        List<ExperienceSimulationRunner> runners = List
            .of(new PerformabilityPcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(pcmMeasurementSpecs);
        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                staticEnvDynModel);
        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider, environmentVariableValueProvider);
        Smodel smodel = getSmodel();
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                environmentVariableValueProvider);
        String reconfigurationStrategyId = smodel.getModelName();
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(
                reconfigurationStrategyId, monitor, smodelInterpreter, smodelInterpreter, qvtoReconfigurationManager);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        // FIXME: read thresholds from kmodel
        Pair<SimulatedMeasurementSpecification, Threshold> upperThresh = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
        Pair<SimulatedMeasurementSpecification, Threshold> lowerThresh = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
        RewardEvaluator<Double> evaluator = new PerformabilityRewardEvaluation(pcmMeasurementSpecs.get(0),
                pcmMeasurementSpecs.get(1), upperThresh, lowerThresh);

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> experienceSimulator = createExperienceSimulator(
                experiment, pcmMeasurementSpecs, runners, getSimulationParameters(), beforeExecutionInitializable,
                envProcess, getSimulatedExperienceStore(), null, reconfStrategy, reconfigurations, evaluator, false,
                experimentProvider, simulationRunnerHolder);

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfigurationStrategyId);
        TotalRewardCalculation rewardCalculation = new PerformabilityEvaluator(
                getSimulationParameters().getSimulationID(), sampleSpaceId);

        ModelledSimulationExecutor<Double> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                getSimulationParameters(), reconfStrategy, rewardCalculation, experimentProvider,
                qvtoReconfigurationManager);
        return executor;
    }

}
