package org.palladiosimulator.simexp.workflow.launcher;

import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class PrismSimulationExecutorFactory extends BaseSimulationExecutorFactory {

    public SimulationExecutor create(IPrismWorkflowConfiguration workflowConfiguration,
            DescriptionProvider descriptionProvider, Optional<ISeedProvider> seedProvider) {
//        PcmModelLoader.Factory modelLoaderFactory = new PcmModelLoader.Factory();
//        PcmExperienceSimulationExecutorFactory<? extends Number, ?, ? extends SimulatedMeasurementSpecification> factory = new DeltaIoTSimulationExecutorFactory(
//                workflowConfiguration, modelLoaderFactory, new SimulatedExperienceStore<>(descriptionProvider),
//                seedProvider);
//        return factory.create();
        return null;
    }
}
