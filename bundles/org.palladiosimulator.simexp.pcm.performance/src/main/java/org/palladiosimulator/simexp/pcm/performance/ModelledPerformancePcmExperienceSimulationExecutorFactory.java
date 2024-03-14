package org.palladiosimulator.simexp.pcm.performance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.strategy.mape.Monitor;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.DummyVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.KnowledgeLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.PcmMonitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.RuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Kmodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
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

public class ModelledPerformancePcmExperienceSimulationExecutorFactory extends
        PcmExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>, PcmMeasurementSpecification> {

    private final static double UPPER_THRESHOLD_RT = 2.0;
    private final static double LOWER_THRESHOLD_RT = 0.3;

    private final Kmodel kmodel;

    private final EnvironmentProcess<QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess;
    private final InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator;

    public ModelledPerformancePcmExperienceSimulationExecutorFactory(Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn, List<PcmMeasurementSpecification> specs,
            SimulationParameters params, SimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder simulationRunnerHolder, Kmodel kmodel) {
        super(experiment, dbn, specs, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);
        this.kmodel = kmodel;

        PerformanceVaryingInterarrivelRateProcess<QVTOReconfigurator, QVToReconfiguration, Integer> p = new PerformanceVaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        this.envProcess = p.getEnvironmentProcess();

        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new HashSet<>(specs);
        this.initialStateCreator = new InitialPcmStateCreator<>(simulatedMeasurementSpecs, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);
    }

    @Override
    public ModelledSimulationExecutor<Integer> create() {

        List<ExperienceSimulationRunner> runners = List
            .of(new PcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        Initializable beforeExecution = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);

        // Monitor PcmMonitor
        // Analyzer KmodelInterpreter
        // Planner KmodelInterpreter
        // Executor not required

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(specs);
        DummyVariableValueProvider vvp = new DummyVariableValueProvider();
        // DummyProbeValueProvider pvp = new DummyProbeValueProvider();
        List<Probe> probes = findProbes(kmodel);
        PcmProbeValueProvider pvp = new PcmProbeValueProvider(probes, specs);
        RuntimeValueProvider rvp = new KnowledgeLookup(null);

        Monitor monitor = new PcmMonitor(simSpecs, pvp);
        SmodelInterpreter kmodelInterpreter = new SmodelInterpreter(kmodel, vvp, pvp, rvp);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(monitor,
                kmodelInterpreter, kmodelInterpreter);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        // FIXME: read thresholds from kmodel
        Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(specs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(threshold);
        boolean isHidden = false;

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> experienceSimulator = createExperienceSimulator(
                experiment, specs, runners, params, beforeExecution, envProcess, simulatedExperienceStore, null,
                reconfStrategy, reconfigurations, evaluator, isHidden);

        // FIXME: make policyId configurable via dsl
        String reconfigurationId = "ModelledReconfigurationStrategy";
        String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(),
                reconfigurationId);
        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator.of(params.getSimulationID(),
                sampleSpaceId);

        ModelledSimulationExecutor<Integer> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                params, reconfStrategy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
        return executor;
    }

    private List<Probe> findProbes(Kmodel kmodel) {
        return kmodel.getProbes();
    }

}
