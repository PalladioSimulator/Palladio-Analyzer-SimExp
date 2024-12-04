package org.palladiosimulator.simexp.pcm.modelled.simulator;

import java.util.List;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.modelled.ModelledExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.simulator.provider.PcmMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.random.ISeedProvider;

public abstract class ModelledPcmExperienceSimulationExecutorFactory<R extends Number, V>
        extends ModelledExperienceSimulationExecutorFactory<R, V, PcmMeasurementSpecification> {

    public ModelledPcmExperienceSimulationExecutorFactory(IModelledPcmWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore, ISeedProvider seedProvider) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider);
    }

    @Override
    protected IModelledPcmWorkflowConfiguration getWorkflowConfiguration() {
        return (IModelledPcmWorkflowConfiguration) super.getWorkflowConfiguration();
    }

    @Override
    protected List<PcmMeasurementSpecification> createSpecs(Experiment experiment) {
        PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(
                getWorkflowConfiguration(), experiment);
        List<PcmMeasurementSpecification> pcmSpecs = provider.getSpecifications();
        return pcmSpecs;
    }
}
