package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.commons.constants.model.SimulationKind;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.simexp.workflow.provider.PcmMeasurementSpecificationProvider;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class PcmSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(SimulationKind simulationKind, Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            List<String> monitorNames) {
        SimulationRunnerHolder simulationRunnerHolder = new SimulationRunnerHolder();
        IQVToReconfigurationManager qvtoReconfigurationManager = new QVToReconfigurationManager(
                getReconfigurationRulesLocation(experiment));
        IExperimentProvider experimentProvider = new ExperimentProvider(experiment);
        PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(experiment);
        List<PcmMeasurementSpecification> pcmSpecs = monitorNames.stream()
            .map(provider::getSpecification)
            .toList();

        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (simulationKind) {
        case PERFORMANCE -> new LoadBalancingSimulationExecutorFactory(experiment, dbn, pcmSpecs, simulationParameters,
                new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);

        case RELIABILITY -> new RobotCognitionSimulationExecutorFactory(experiment, dbn, pcmSpecs, simulationParameters,
                new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder);

        case PERFORMABILITY -> new FaultTolerantLoadBalancingSimulationExecutorFactory(experiment, dbn, pcmSpecs,
                simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser, probDistRepoLookup,
                experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder);

//            case MODELLED -> new ModelledPerformabilityPcmExperienceSimulationExecutorFactory(experiment, dbn, pcmSpecs,
//                    simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
//                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
//                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel,
//                    staticEnvDynModel);

        default -> throw new RuntimeException("Unexpected quality objective " + simulationKind);
        };
        return factory.create();
    }
}
