package org.palladiosimulator.simexp.pcm.modelled;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public abstract class ModelledExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification>
        extends PcmExperienceSimulationExecutorFactory<R, V, T> {
    private final Smodel smodel;

    public ModelledExperienceSimulationExecutorFactory(IModelledWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader modelLoader, SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore) {
        super(workflowConfiguration, modelLoader, simulatedExperienceStore);
        URI smodelURI = workflowConfiguration.getSmodelURI();
        this.smodel = modelLoader.loadSModel(rs, smodelURI);
    }

    protected Smodel getSmodel() {
        return smodel;
    }
}