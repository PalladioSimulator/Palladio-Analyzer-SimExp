package org.palladiosimulator.simexp.workflow.launcher;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.URI;
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
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
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
    public SimulationExecutor create(SimulationEngine simulationEngine, QualityObjective qualityObjective,
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            List<String> monitorNames, List<URI> propertyFiles, List<URI> moduleFiles, Smodel smodel,
            ProbabilisticModelRepository staticEnvDynModel) {
        return switch (simulationEngine) {
        case PCM -> {
            yield createPCM(qualityObjective, experiment, dbn, probabilityDistributionRegistry,
                    probabilityDistributionFactory, parameterParser, probDistRepoLookup, simulationParameters,
                    descriptionProvider, monitorNames, smodel, staticEnvDynModel);
        }
        case PRISM -> {
            yield createPRISM(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory,
                    parameterParser, probDistRepoLookup, simulationParameters, descriptionProvider, propertyFiles,
                    moduleFiles, smodel, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + simulationEngine);
        };
    }

    private SimulationExecutor createPCM(QualityObjective qualityObjective, Experiment experiment,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider,
            List<String> monitorNames, Smodel smodel, ProbabilisticModelRepository staticEnvDynModel) {
        SimulationRunnerHolder simulationRunnerHolder = new SimulationRunnerHolder();
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        IExperimentProvider experimentProvider = new ExperimentProvider(experiment);
        PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(experiment);
        List<PcmMeasurementSpecification> pcmSpecs = monitorNames.stream()
            .map(provider::getSpecification)
            .toList();

        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> {
            yield new ModelledPerformancePcmExperienceSimulationExecutorFactory(experiment, dbn, pcmSpecs,
                    simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel,
                    staticEnvDynModel);
        }
        case RELIABILITY -> {
            yield new ModelledReliabilityPcmExperienceSimulationExecutorFactory(experiment, dbn, pcmSpecs,
                    simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel,
                    staticEnvDynModel);
        }
        case PERFORMABILITY -> {
            yield new ModelledPerformabilityPcmExperienceSimulationExecutorFactory(experiment, dbn, pcmSpecs,
                    simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                    probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser,
                    probDistRepoLookup, experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel,
                    staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("QualityObjective not supported: " + qualityObjective);
        };

        return factory.create();
    }

    private SimulationExecutor createPRISM(Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            SimulationParameters simulationParameters, DescriptionProvider descriptionProvider, List<URI> propertyFiles,
            List<URI> moduleFiles, Smodel smodel, ProbabilisticModelRepository staticEnvDynModel) {
        SimulationRunnerHolder simulationRunnerHolder = new SimulationRunnerHolder();
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        IExperimentProvider experimentProvider = new ExperimentProvider(experiment);
        PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider();
        List<PrismSimulatedMeasurementSpec> prismSpecs = IntStream
            .range(0, Math.min(propertyFiles.size(), moduleFiles.size()))
            .mapToObj(i -> provider.getSpecification(moduleFiles.get(i), propertyFiles.get(i)))
            .toList();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new ModelledPrismPcmExperienceSimulationExecutorFactory(
                experiment, dbn, prismSpecs, simulationParameters, new SimulatedExperienceStore<>(descriptionProvider),
                probabilityDistributionFactory, probabilityDistributionRegistry, parameterParser, probDistRepoLookup,
                experimentProvider, qvtoReconfigurationManager, simulationRunnerHolder, smodel, staticEnvDynModel);
        return factory.create();
    }

}
