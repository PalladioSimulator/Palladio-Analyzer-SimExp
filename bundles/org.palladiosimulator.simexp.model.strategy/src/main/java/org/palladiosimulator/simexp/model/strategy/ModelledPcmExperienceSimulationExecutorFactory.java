package org.palladiosimulator.simexp.model.strategy;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
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

public class ModelledPcmExperienceSimulationExecutorFactory<R extends Number> extends
        PcmExperienceSimulationExecutorFactory<R, List<InputValue<CategoricalValue>>, PcmMeasurementSpecification> {
    private final Kmodel kmodel;

    public ModelledPcmExperienceSimulationExecutorFactory(Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn, List<PcmMeasurementSpecification> specs,
            SimulationParameters params, SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder simulationRunnerHolder, Kmodel kmodel) {
        super(experiment, dbn, specs, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);
        this.kmodel = kmodel;
    }

    @Override
    public ModelledSimulationExecutor<R> create() {
        // TODO: remove
        kmodel.getClass();

        List<ExperienceSimulationRunner> runners = null;
        Initializable beforeExecution = null;
        EnvironmentProcess<QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> envProcess = null;
        SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> navigator = null;
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = null;
        Set<QVToReconfiguration> reconfigurations = null;
        RewardEvaluator<R> evaluator = null;
        boolean hidden = false;

        TotalRewardCalculation rewardCalculation = null;

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, R> experienceSimulator = createExperienceSimulator(
                experiment, specs, runners, params, beforeExecution, envProcess, simulatedExperienceStore, navigator,
                reconfStrategy, reconfigurations, evaluator, hidden);

        ModelledSimulationExecutor<R> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                params, reconfStrategy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
        return executor;

    }

}
