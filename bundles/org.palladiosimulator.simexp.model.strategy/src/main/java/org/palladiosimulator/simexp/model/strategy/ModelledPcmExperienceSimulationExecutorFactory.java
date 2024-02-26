package org.palladiosimulator.simexp.model.strategy;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
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
        ExperienceSimulator<PCMInstance, QVTOReconfigurator, R> experienceSimulator =
        createExperienceSimulator(experiment, specs, 
                List<ExperienceSimulationRunner> runners,
         params, 
        Initializable beforeExecution,
        EnvironmentProcess<QVTOReconfigurator, R, V> envProcess,
        simulatedExperienceStore,
        SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, V> navigator,
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy, 
        Set<QVToReconfiguration> reconfigurations, 
        RewardEvaluator<R> evaluator
        , boolean hidden);
        
        /*return new ModelledSimulationExecutor<>(experienceSimulator, experiment, params,
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy,
        TotalRewardCalculation rewardCalculation, 
        experimentProvider, qvtoReconfigurationManager,
        kmodel);*/
        
        return null;
    }

}
