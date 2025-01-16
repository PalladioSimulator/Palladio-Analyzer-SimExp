package org.palladiosimulator.simexp.pcm.modelled.prism;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.provider.PrismMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.modelled.ModelledExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.random.ISeedProvider;

public abstract class ModelledPrismExperienceSimulationExecutorFactory<R extends Number, V>
        extends ModelledExperienceSimulationExecutorFactory<R, V, PrismSimulatedMeasurementSpec> {

    public ModelledPrismExperienceSimulationExecutorFactory(IModelledPrismWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, SimulatedExperienceAccessor accessor) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor);
    }

    @Override
    protected IModelledPrismWorkflowConfiguration getWorkflowConfiguration() {
        return (IModelledPrismWorkflowConfiguration) super.getWorkflowConfiguration();
    }

    @Override
    protected List<PrismSimulatedMeasurementSpec> createSpecs(Experiment experiment) {
        PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider(
                getWorkflowConfiguration());
        List<PrismSimulatedMeasurementSpec> prismSpecs = provider.getSpecifications();
        return prismSpecs;
    }
}
