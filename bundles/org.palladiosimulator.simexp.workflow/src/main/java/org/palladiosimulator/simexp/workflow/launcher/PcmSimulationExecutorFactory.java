package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.workflow.provider.PcmMeasurementSpecificationProvider;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class PcmSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(IPCMWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            QualityObjective qualityObjective, Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider) {
        PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(workflowConfiguration,
                experiment);
        List<PcmMeasurementSpecification> pcmSpecs = provider.getSpecifications();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> new LoadBalancingSimulationExecutorFactory(workflowConfiguration, rs, experiment, dbn,
                pcmSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser, probDistRepoLookup);

        case RELIABILITY -> new RobotCognitionSimulationExecutorFactory(workflowConfiguration, rs, experiment, dbn,
                pcmSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser, probDistRepoLookup);

        case PERFORMABILITY -> new FaultTolerantLoadBalancingSimulationExecutorFactory(workflowConfiguration, rs,
                experiment, dbn, pcmSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser, probDistRepoLookup);

        default -> throw new RuntimeException("Unexpected QualityObjective: " + qualityObjective);
        };
        return factory.create();
    }
}
