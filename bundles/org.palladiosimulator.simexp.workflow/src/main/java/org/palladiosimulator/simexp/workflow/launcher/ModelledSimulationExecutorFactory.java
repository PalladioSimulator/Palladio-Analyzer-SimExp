package org.palladiosimulator.simexp.workflow.launcher;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.prism.ModelledPrismPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.prism.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.performability.ModelledPerformabilityPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.performance.ModelledPerformancePcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.reliability.ModelledReliabilityPcmExperienceSimulationExecutorFactory;

public class ModelledSimulationExecutorFactory extends BaseSimulationExecutorFactory {
    public SimulationExecutor create(IModelledWorkflowConfiguration workflowConfiguration,
            DescriptionProvider descriptionProvider) {
        PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
        SimulationEngine simulationEngine = workflowConfiguration.getSimulationEngine();
        return switch (simulationEngine) {
        case PCM -> {
            yield createPCM((IModelledPcmWorkflowConfiguration) workflowConfiguration, modelLoaderFactory,
                    descriptionProvider);
        }
        case PRISM -> {
            yield createPRISM((IModelledPrismWorkflowConfiguration) workflowConfiguration, modelLoaderFactory,
                    descriptionProvider);
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + simulationEngine);
        };
    }

    private SimulationExecutor createPCM(IModelledPcmWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader.Factory modelLoaderFactory, DescriptionProvider descriptionProvider) {
        QualityObjective qualityObjective = workflowConfiguration.getQualityObjective();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> {
            yield new ModelledPerformancePcmExperienceSimulationExecutorFactory(workflowConfiguration,
                    modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider));
        }
        case RELIABILITY -> {
            yield new ModelledReliabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration,
                    modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider));
        }
        case PERFORMABILITY -> {
            yield new ModelledPerformabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration,
                    modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider));
        }
        default -> throw new IllegalArgumentException("QualityObjective not supported: " + qualityObjective);
        };

        return factory.create();
    }

    private SimulationExecutor createPRISM(IModelledPrismWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader.Factory modelLoaderFactory, DescriptionProvider descriptionProvider) {
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new ModelledPrismPcmExperienceSimulationExecutorFactory(
                workflowConfiguration, modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider));
        return factory.create();
    }

}
