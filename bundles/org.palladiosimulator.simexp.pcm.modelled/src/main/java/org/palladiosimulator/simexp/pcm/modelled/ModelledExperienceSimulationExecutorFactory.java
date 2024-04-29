package org.palladiosimulator.simexp.pcm.modelled;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

public abstract class ModelledExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification>
        extends PcmExperienceSimulationExecutorFactory<R, V, T> {

    public ModelledExperienceSimulationExecutorFactory(IModelledWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader modelLoader, SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore) {
        super(workflowConfiguration, modelLoader, simulatedExperienceStore);
    }

    @Override
    protected IModelledWorkflowConfiguration getWorkflowConfiguration() {
        return (IModelledWorkflowConfiguration) super.getWorkflowConfiguration();
    }

    @Override
    protected ModelledModelLoader getModelLoader() {
        return (ModelledModelLoader) super.getModelLoader();
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> doCreate() {
        URI smodelURI = getWorkflowConfiguration().getSmodelURI();
        Smodel smodel = getModelLoader().loadSModel(rs, smodelURI);
        return doModelledCreate(smodel);
    }

    protected abstract PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> doModelledCreate(
            Smodel smodel);
}