package org.palladiosimulator.simexp.workflow.launcher;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.prism.ModelledPrismPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.prism.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.performability.ModelledPerformabilityPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.performance.ModelledPerformancePcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.reliability.ModelledReliabilityPcmExperienceSimulationExecutorFactory;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;

public class ModelledSimulationExecutorFactory extends BaseSimulationExecutorFactory {
    public SimulationExecutor create(IModelledWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            IProbabilityDistributionRepositoryLookup probDistRepoLookup, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        SimulationEngine simulationEngine = workflowConfiguration.getSimulationEngine();
        return switch (simulationEngine) {
        case PCM -> {
            yield createPCM((IModelledPcmWorkflowConfiguration) workflowConfiguration, rs, dbn,
                    probabilityDistributionRegistry, probabilityDistributionFactory, probDistRepoLookup,
                    descriptionProvider, staticEnvDynModel);
        }
        case PRISM -> {
            yield createPRISM((IModelledPrismWorkflowConfiguration) workflowConfiguration, rs, dbn,
                    probabilityDistributionRegistry, probabilityDistributionFactory, probDistRepoLookup,
                    descriptionProvider, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + simulationEngine);
        };
    }

    private SimulationExecutor createPCM(IModelledPcmWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            IProbabilityDistributionRepositoryLookup probDistRepoLookup, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        QualityObjective qualityObjective = workflowConfiguration.getQualityObjective();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> {
            yield new ModelledPerformancePcmExperienceSimulationExecutorFactory(workflowConfiguration, rs, dbn,
                    new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                    probabilityDistributionRegistry, probDistRepoLookup, staticEnvDynModel);
        }
        case RELIABILITY -> {
            yield new ModelledReliabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration, rs, dbn,
                    new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                    probabilityDistributionRegistry, probDistRepoLookup, staticEnvDynModel);
        }
        case PERFORMABILITY -> {
            yield new ModelledPerformabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration, rs, dbn,
                    new SimulatedExperienceStore<>(descriptionProvider), probabilityDistributionFactory,
                    probabilityDistributionRegistry, probDistRepoLookup, staticEnvDynModel);
        }
        default -> throw new IllegalArgumentException("QualityObjective not supported: " + qualityObjective);
        };

        return factory.create();
    }

    private SimulationExecutor createPRISM(IModelledPrismWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            DynamicBayesianNetwork<CategoricalValue> dbn,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionFactory<CategoricalValue> probabilityDistributionFactory,
            IProbabilityDistributionRepositoryLookup probDistRepoLookup, DescriptionProvider descriptionProvider,
            ProbabilisticModelRepository staticEnvDynModel) {
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new ModelledPrismPcmExperienceSimulationExecutorFactory(
                workflowConfiguration, rs, dbn, new SimulatedExperienceStore<>(descriptionProvider),
                probabilityDistributionFactory, probabilityDistributionRegistry, probDistRepoLookup, staticEnvDynModel);
        return factory.create();
    }

}
