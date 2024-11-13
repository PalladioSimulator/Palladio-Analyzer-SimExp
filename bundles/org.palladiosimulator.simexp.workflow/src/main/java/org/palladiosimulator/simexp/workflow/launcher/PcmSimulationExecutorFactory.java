package org.palladiosimulator.simexp.workflow.launcher;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;

public class PcmSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(IPCMWorkflowConfiguration workflowConfiguration,
            DescriptionProvider descriptionProvider) {
        PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
        QualityObjective qualityObjective = workflowConfiguration.getQualityObjective();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> new LoadBalancingSimulationExecutorFactory(workflowConfiguration, modelLoaderFactory,
                new SimulatedExperienceStore<>(descriptionProvider));

        case RELIABILITY -> new RobotCognitionSimulationExecutorFactory(workflowConfiguration, modelLoaderFactory,
                new SimulatedExperienceStore<>(descriptionProvider));

        case PERFORMABILITY -> new FaultTolerantLoadBalancingSimulationExecutorFactory(workflowConfiguration,
                modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider));

        default -> throw new RuntimeException("Unexpected QualityObjective: " + qualityObjective);
        };
        return factory.create();
    }
}
