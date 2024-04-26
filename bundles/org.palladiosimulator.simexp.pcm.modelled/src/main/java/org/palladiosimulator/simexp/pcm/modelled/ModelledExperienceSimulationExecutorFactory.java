package org.palladiosimulator.simexp.pcm.modelled;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.model.io.SModelLoader;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;

public abstract class ModelledExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification>
        extends PcmExperienceSimulationExecutorFactory<R, V, T> {
    private static final Logger LOGGER = Logger.getLogger(ModelledExperienceSimulationExecutorFactory.class);

    private final Smodel smodel;

    public ModelledExperienceSimulationExecutorFactory(IModelledWorkflowConfiguration workflowConfiguration,
            ResourceSet rs, DynamicBayesianNetwork<CategoricalValue> dbn, SimulationParameters params,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionRepositoryLookup probDistRepoLookup) {
        super(workflowConfiguration, rs, dbn, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, probDistRepoLookup);
        URI smodelURI = workflowConfiguration.getSmodelURI();
        SModelLoader smodelLoader = new SModelLoader();
        this.smodel = smodelLoader.load(rs, smodelURI);
        LOGGER.debug(String.format("Loaded smodel from '%s'", smodelURI.path()));
    }

    protected Smodel getSmodel() {
        return smodel;
    }

}