package org.palladiosimulator.simexp.workflow.launcher;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;

public class PrismSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(IPrismWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            DescriptionProvider descriptionProvider) {
        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new DeltaIoTSimulationExecutorFactory(
                workflowConfiguration, rs, new SimulatedExperienceStore<>(descriptionProvider));
        return factory.create();
    }
}
