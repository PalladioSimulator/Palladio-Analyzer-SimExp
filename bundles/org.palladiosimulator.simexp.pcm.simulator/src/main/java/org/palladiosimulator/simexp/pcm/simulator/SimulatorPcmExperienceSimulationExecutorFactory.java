package org.palladiosimulator.simexp.pcm.simulator;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.simulator.provider.PcmMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.random.ISeedProvider;

public abstract class SimulatorPcmExperienceSimulationExecutorFactory<R extends Number, V>
        extends PcmExperienceSimulationExecutorFactory<R, V, PcmMeasurementSpecification> {
    public SimulatorPcmExperienceSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor,
                resourcePath);
    }

    @Override
    protected IPCMWorkflowConfiguration getWorkflowConfiguration() {
        return (IPCMWorkflowConfiguration) super.getWorkflowConfiguration();
    }

    @Override
    protected List<PcmMeasurementSpecification> createSpecs(Experiment experiment) {
        PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(
                getWorkflowConfiguration(), experiment);
        List<PcmMeasurementSpecification> pcmSpecs = provider.getSpecifications();
        return pcmSpecs;
    }
}
