package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.provider.PrismMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class PrismSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(IPrismWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider) {
        PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider(
                workflowConfiguration);
        List<PrismSimulatedMeasurementSpec> prismSpecs = provider.getSpecifications();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new DeltaIoTSimulationExecutorFactory(
                workflowConfiguration, rs, experiment, dbn, prismSpecs, simulationParameters,
                new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup);
        return factory.create();
    }
}
