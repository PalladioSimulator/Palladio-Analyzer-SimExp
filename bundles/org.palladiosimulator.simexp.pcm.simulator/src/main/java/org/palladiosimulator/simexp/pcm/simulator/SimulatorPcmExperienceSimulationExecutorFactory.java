package org.palladiosimulator.simexp.pcm.simulator;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.simulator.provider.PcmMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;

public abstract class SimulatorPcmExperienceSimulationExecutorFactory<R extends Number, V>
        extends PcmExperienceSimulationExecutorFactory<R, V, PcmMeasurementSpecification> {
    public SimulatorPcmExperienceSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration,
            ResourceSet rs, DynamicBayesianNetwork<CategoricalValue> dbn,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            IProbabilityDistributionRepositoryLookup probDistRepoLookup) {
        super(workflowConfiguration, rs, dbn, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, probDistRepoLookup);
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
