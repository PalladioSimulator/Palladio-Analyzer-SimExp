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
import org.palladiosimulator.simexp.core.strategy.mape.pcm.PcmMonitor;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.lookup.IModelNameLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.FieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.pcm.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
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

    private final Smodel smodel;

    private final EnvironmentProcess<QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess;
    private final InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator;
    private final IModelNameLookup modelNameLookup;

    public ModelledPerformancePcmExperienceSimulationExecutorFactory(Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn, List<PcmMeasurementSpecification> specs,
            SimulationParameters params, SimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder simulationRunnerHolder, Smodel smodel, IModelNameLookup modelNameLookup) {
        super(experiment, dbn, specs, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);
        this.smodel = smodel;
        this.modelNameLookup = modelNameLookup;

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
        // Analyzer SmodelInterpreter
        // Planner SmodelInterpreter
        // Executor not required

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(specs);
        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        // TODO:
        IFieldValueProvider optimizableValueProvider = null;
        IFieldValueProvider fieldValueProvider = new FieldValueProvider(probeValueProvider, optimizableValueProvider);

        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider);
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, fieldValueProvider);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(monitor,
                smodelInterpreter, smodelInterpreter);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        // FIXME: read thresholds from smodel
        Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(specs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(threshold);
        boolean isHidden = false;

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> experienceSimulator = createExperienceSimulator(
                experiment, specs, runners, params, beforeExecution, envProcess, simulatedExperienceStore, null,
                reconfStrategy, reconfigurations, evaluator, isHidden);

        String reconfigurationId = modelNameLookup.findModelName();
        String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(),
                reconfigurationId);
        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator.of(params.getSimulationID(),
                sampleSpaceId);

        ModelledSimulationExecutor<Integer> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                params, reconfStrategy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
        return executor;
    }

    private List<Probe> findProbes(Smodel smodel) {
        return smodel.getProbes();
    }

}
