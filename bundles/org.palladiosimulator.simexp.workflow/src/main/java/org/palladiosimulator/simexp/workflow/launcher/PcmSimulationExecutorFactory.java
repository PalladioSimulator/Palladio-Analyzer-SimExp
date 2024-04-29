package org.palladiosimulator.simexp.workflow.launcher;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;

public class PcmSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(IPCMWorkflowConfiguration workflowConfiguration, ModelLoader modelLoader,
            ResourceSet rs, DescriptionProvider descriptionProvider) {
        QualityObjective qualityObjective = workflowConfiguration.getQualityObjective();
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = switch (qualityObjective) {
        case PERFORMANCE -> new LoadBalancingSimulationExecutorFactory(workflowConfiguration, modelLoader, rs,
                new SimulatedExperienceStore<>(descriptionProvider));

        case RELIABILITY -> new RobotCognitionSimulationExecutorFactory(workflowConfiguration, modelLoader, rs,
                new SimulatedExperienceStore<>(descriptionProvider));

        case PERFORMABILITY -> new FaultTolerantLoadBalancingSimulationExecutorFactory(workflowConfiguration,
                modelLoader, rs, new SimulatedExperienceStore<>(descriptionProvider));

        default -> throw new RuntimeException("Unexpected QualityObjective: " + qualityObjective);
        };
        return factory.create();
    }
}
