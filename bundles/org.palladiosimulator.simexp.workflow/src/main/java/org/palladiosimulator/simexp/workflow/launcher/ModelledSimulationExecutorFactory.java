package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.performability.ModelledPerformabilityPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.performance.ModelledPerformancePcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.ModelledReliabilityPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.workflow.provider.PcmMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.workflow.provider.PrismMeasurementSpecificationProvider;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class ModelledSimulationExecutorFactory extends BaseSimulationExecutorFactory {
    public SimulationExecutor create(IModelledWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            SimulationEngine simulationEngine, QualityObjective qualityObjective, Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        return switch (simulationEngine) {
        case PCM -> {
            yield createPCM((IModelledPcmWorkflowConfiguration) workflowConfiguration, rs, qualityObjective, experiment,
                    dbn, probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser,
                    probDistRepoLookup, simulationParameters, descriptionProvider, staticEnvDynModel);
        }
        case PRISM -> {
            yield createPRISM((IModelledPrismWorkflowConfiguration) workflowConfiguration, rs, experiment, dbn,
                    probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser,
                    probDistRepoLookup, simulationParameters, descriptionProvider, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + simulationEngine);
        };
    }

    private SimulationExecutor createPCM(IModelledPcmWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            QualityObjective qualityObjective, Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        SimulationRunnerHolder simulationRunnerHolder = new SimulationRunnerHolder();
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        IExperimentProvider experimentProvider = new ExperimentProvider(experiment);
        PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(experiment);
        List<String> monitorNames = workflowConfiguration.getMonitorNames();
        List<PcmMeasurementSpecification> pcmSpecs = monitorNames.stream()
            .map(provider::getSpecification)
            .toList();

        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> {
            yield new ModelledPerformancePcmExperienceSimulationExecutorFactory(workflowConfiguration, rs, experiment,
                    dbn, pcmSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder,
                    staticEnvDynModel);
        }
        case RELIABILITY -> {
            yield new ModelledReliabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration, rs, experiment,
                    dbn, pcmSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder,
                    staticEnvDynModel);
        }
        case PERFORMABILITY -> {
            yield new ModelledPerformabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration, rs,
                    experiment, dbn, pcmSpecs, simulationParameters,
                    new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                    probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                    qvtoReconfigurationManager, simulationRunnerHolder, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("QualityObjective not supported: " + qualityObjective);
        };

        return factory.create();
    }

    private SimulationExecutor createPRISM(IModelledPrismWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        SimulationRunnerHolder simulationRunnerHolder = new SimulationRunnerHolder();
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        IExperimentProvider experimentProvider = new ExperimentProvider(experiment);
        PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider();
        List<URI> propertyFiles = workflowConfiguration.getPropertyFiles();
        List<URI> moduleFiles = workflowConfiguration.getModuleFiles();
        List<PrismSimulatedMeasurementSpec> prismSpecs = IntStream
            .range(0, Math.min(propertyFiles.size(), moduleFiles.size()))
            .mapToObj(i -> provider.getSpecification(moduleFiles.get(i), propertyFiles.get(i)))
            .toList();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new ModelledPrismPcmExperienceSimulationExecutorFactory(
                workflowConfiguration, rs, experiment, dbn, prismSpecs, simulationParameters,
                new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                qvtoReconfigurationManager, simulationRunnerHolder, staticEnvDynModel);
        return factory.create();
    }

}
