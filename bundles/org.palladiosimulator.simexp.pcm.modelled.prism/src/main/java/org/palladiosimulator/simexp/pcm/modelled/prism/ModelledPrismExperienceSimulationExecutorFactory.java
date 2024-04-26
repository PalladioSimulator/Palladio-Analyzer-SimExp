package org.palladiosimulator.simexp.pcm.modelled.prism;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.modelled.ModelledExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public abstract class ModelledPrismExperienceSimulationExecutorFactory<R extends Number, V>
        extends ModelledExperienceSimulationExecutorFactory<R, V, PrismSimulatedMeasurementSpec> {

    private final List<PrismSimulatedMeasurementSpec> specs;

    public ModelledPrismExperienceSimulationExecutorFactory(IModelledWorkflowConfiguration workflowConfiguration,
            ResourceSet rs, Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            List<PrismSimulatedMeasurementSpec> specs, SimulationParameters params,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup) {
        super(workflowConfiguration, rs, experiment, dbn, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup);
        this.specs = specs;
    }

    @Override
    protected List<PrismSimulatedMeasurementSpec> createSpecs() {
        return specs;
    }
}
