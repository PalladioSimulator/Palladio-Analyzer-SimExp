package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.workflow.provider.PrismMeasurementSpecificationProvider;

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
        SimulationRunnerHolder simulationRunnerHolder = new SimulationRunnerHolder();
        IExperimentProvider experimentProvider = new ExperimentProvider(experiment);
        PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider();
        List<URI> propertyFiles = workflowConfiguration.getPropertyFiles();
        List<URI> moduleFiles = workflowConfiguration.getModuleFiles();
        List<PrismSimulatedMeasurementSpec> prismSpecs = IntStream
            .range(0, Math.min(propertyFiles.size(), moduleFiles.size()))
            .mapToObj(i -> provider.getSpecification(moduleFiles.get(i), propertyFiles.get(i)))
            .toList();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new DeltaIoTSimulationExecutorFactory(
                workflowConfiguration, rs, experiment, dbn, prismSpecs, simulationParameters,
                new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                simulationRunnerHolder);
        return factory.create();
    }
}
