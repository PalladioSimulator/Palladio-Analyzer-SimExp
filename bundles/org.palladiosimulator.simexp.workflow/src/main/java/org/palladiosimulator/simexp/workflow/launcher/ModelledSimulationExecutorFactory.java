package org.palladiosimulator.simexp.workflow.launcher;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledSimulationExecutorFactory extends BaseSimulationExecutorFactory {
    public SimulationExecutor create(IModelledWorkflowConfiguration workflowConfiguration,
            DescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider) {
        /*
         * PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
         * SimulationEngine simulationEngine = workflowConfiguration.getSimulationEngine();
         * 
         * return switch (simulationEngine) { case PCM -> { yield
         * createPCM((IModelledPcmWorkflowConfiguration) workflowConfiguration, modelLoaderFactory,
         * descriptionProvider, seedProvider); } case PRISM -> { yield
         * createPRISM((IModelledPrismWorkflowConfiguration) workflowConfiguration,
         * modelLoaderFactory, descriptionProvider, seedProvider); } default -> throw new
         * IllegalArgumentException("Unexpected value: " + simulationEngine); };
         */
        return null;
    }

//    private SimulationExecutor createPCM(IModelledPcmWorkflowConfiguration workflowConfiguration,
//            ModelledModelLoader.Factory modelLoaderFactory, DescriptionProvider descriptionProvider,
//            Optional<ISeedProvider> seedProvider) {
//        QualityObjective qualityObjective = workflowConfiguration.getQualityObjective();
//        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
//
//        /*
//         * case PERFORMANCE -> { yield new
//         * ModelledPerformancePcmExperienceSimulationExecutorFactory(workflowConfiguration,
//         * modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider), seedProvider); }
//         */
//
//        /*
//        case RELIABILITY -> {
//            yield new ModelledReliabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration,
//                    modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider), seedProvider);
//        }*/
//        /*
//         * case PERFORMABILITY -> { yield new
//         * ModelledPerformabilityPcmExperienceSimulationExecutorFactory(workflowConfiguration,
//         * modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider), seedProvider); }
//         */
//        default -> throw new IllegalArgumentException("QualityObjective not supported: " + qualityObjective);
//        };
//
//        return factory.create();
//    }

    /*
     * private SimulationExecutor createPRISM(IModelledPrismWorkflowConfiguration
     * workflowConfiguration, ModelledModelLoader.Factory modelLoaderFactory, DescriptionProvider
     * descriptionProvider, Optional<ISeedProvider> seedProvider) {
     * PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends
     * SimulatedMeasurementSpecification> factory = new
     * ModelledPrismPcmExperienceSimulationExecutorFactory( workflowConfiguration,
     * modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider), seedProvider);
     * return factory.create(); }
     */
}
