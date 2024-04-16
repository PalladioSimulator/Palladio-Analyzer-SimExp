package org.palladiosimulator.simexp.pcm.performability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.OptimizableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class ModelledPerformabilityPcmExperienceSimulationExecutorFactory extends
        PcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>, PcmMeasurementSpecification> {

    public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);

    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";

    private final Smodel smodel;
    private final ProbabilisticModelRepository staticEnvDynModel;

    private final EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess;
    private final InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator;

    public ModelledPerformabilityPcmExperienceSimulationExecutorFactory(Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn, List<PcmMeasurementSpecification> specs,
            SimulationParameters params, SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder simulationRunnerHolder, Smodel smodel,
            ProbabilisticModelRepository staticEnvDynModel) {
        super(experiment, dbn, specs, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);
        this.smodel = smodel;
        this.staticEnvDynModel = staticEnvDynModel;

        PerformabilityVaryingInterarrivelRateProcess<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> p = new PerformabilityVaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        this.envProcess = p.getEnvironmentProcess();

        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new HashSet<>(specs);
        this.initialStateCreator = new InitialPcmStateCreator<>(simulatedMeasurementSpecs, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);
    }

    @Override
    public ModelledSimulationExecutor<Double> create() {
        List<ExperienceSimulationRunner> runners = List
            .of(new PerformabilityPcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);

        // TODO: old reconfiguration strategy, which shall be replaced by the modelled one
//            PerformabilityStrategyConfiguration config = new PerformabilityStrategyConfiguration(SERVER_FAILURE_TEMPLATE_ID,
//                    LOAD_BALANCER_ID);
//            NodeRecoveryStrategy<PCMInstance, QVTOReconfigurator> nodeRecoveryStrategy = new FaultTolerantScalingNodeFailureRecoveryStrategy<>(
//                    config, new RepositoryModelLookup(), new ResourceEnvironmentModelLookup(),
//                    new RepositoryModelUpdater());
//            LoadBalancingEmptyReconfigurationPlanningStrategy<PCMInstance, QVTOReconfigurator> emptyStrategy = new LoadBalancingEmptyReconfigurationPlanningStrategy<>(
//                    specs.get(0), config, nodeRecoveryStrategy);
//            ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new PerformabilityStrategy<>(
//                    specs.get(0), config, emptyStrategy);
//            Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = reconfStrategy;

        // Monitor PcmMonitor
        // Analyzer KmodelInterpreter
        // Planner KmodelInterpreter
        // Executor not required

//        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(specs);
//        DummyVariableValueProvider vvp = new DummyVariableValueProvider();
        // DummyProbeValueProvider pvp = new DummyProbeValueProvider();
//        List<Probe> probes = findProbes(kmodel);
//        PcmProbeValueProvider pvp = new PcmProbeValueProvider(probes, specs);
//        RuntimeValueProvider rvp = new KnowledgeLookup(null);

//        Monitor monitor = new PcmMonitor(simSpecs, pvp);
//        KmodelInterpreter kmodelInterpreter = new KmodelInterpreter(kmodel, vvp, pvp, rvp);
//        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(monitor,
//                kmodelInterpreter, kmodelInterpreter);

//        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(specs);
        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        // TODO: rework; introduce lookup interface to find GVRs instead of passing
        // staticEnvDynModel directly
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                staticEnvDynModel);
        OptimizableValueProvider optimizableValueProvider = new OptimizableValueProvider();
        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider, environmentVariableValueProvider);
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                optimizableValueProvider);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(monitor,
                smodelInterpreter, smodelInterpreter);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        // FIXME: read thresholds from kmodel
        Pair<SimulatedMeasurementSpecification, Threshold> upperThresh = Pair.of(specs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
        Pair<SimulatedMeasurementSpecification, Threshold> lowerThresh = Pair.of(specs.get(0),
                Threshold.lessThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
        RewardEvaluator<Double> evaluator = new PerformabilityRewardEvaluation(specs.get(0), specs.get(1), upperThresh,
                lowerThresh);

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> experienceSimulator = createExperienceSimulator(
                experiment, specs, runners, params, beforeExecutionInitializable, envProcess, simulatedExperienceStore,
                null, reconfStrategy, reconfigurations, evaluator, false);

        String reconfigurationId = smodel.getModelName();
        String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(),
                reconfigurationId);
        TotalRewardCalculation rewardCalculation = new PerformabilityEvaluator(params.getSimulationID(), sampleSpaceId);

        ModelledSimulationExecutor<Double> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                params, reconfStrategy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
        return executor;
    }

//    private List<Probe> findProbes(Kmodel kmodel) {
//        List<Probe> probes = new ArrayList<>();
//        EList<Field> fields = kmodel.getFields();
//
//        for (Field f : kmodel.getFields()) {
//            if (f instanceof Probe) {
//                Probe probe = (Probe) f;
//                probes.add(probe);
//            }
//        }
//        return probes;
//    }

}
