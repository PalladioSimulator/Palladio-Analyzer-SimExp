package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.model.io.SModelLoader;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
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
    private static final Logger LOGGER = Logger.getLogger(ModelledSimulationExecutorFactory.class);

    public SimulationExecutor create(IModelledWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            SimulationEngine simulationEngine, QualityObjective qualityObjective, Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        URI smodelURI = workflowConfiguration.getSmodelURI();
        SModelLoader smodelLoader = new SModelLoader();
        Smodel smodel = smodelLoader.load(rs, smodelURI);
        LOGGER.debug(String.format("Loaded smodel from '%s'", smodelURI.path()));

        return switch (simulationEngine) {
        case PCM -> {
            yield createPCM((IPCMWorkflowConfiguration) workflowConfiguration, rs, qualityObjective, experiment, dbn,
                    probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser,
                    probDistRepoLookup, simulationParameters, descriptionProvider, smodel, staticEnvDynModel);
        }
        case PRISM -> {
            yield createPRISM((IPrismWorkflowConfiguration) workflowConfiguration, rs, experiment, dbn,
                    probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser,
                    probDistRepoLookup, simulationParameters, descriptionProvider, smodel, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + simulationEngine);
        };
    }

    private SimulationExecutor createPCM(IPCMWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            QualityObjective qualityObjective, Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider, Smodel smodel,
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
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel,
                    staticEnvDynModel);
        }
        case RELIABILITY -> {
            yield new ModelledReliabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration, rs, experiment,
                    dbn, pcmSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel,
                    staticEnvDynModel);
        }
        case PERFORMABILITY -> {
            yield new ModelledPerformabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration, rs,
                    experiment, dbn, pcmSpecs, simulationParameters,
                    new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                    probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                    qvtoReconfigurationManager, simulationRunnerHolder, smodel, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("QualityObjective not supported: " + qualityObjective);
        };

        return factory.create();
    }

    private SimulationExecutor createPRISM(IPrismWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider, Smodel smodel,
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
                qvtoReconfigurationManager, simulationRunnerHolder, smodel, staticEnvDynModel);
        return factory.create();
    }

}
