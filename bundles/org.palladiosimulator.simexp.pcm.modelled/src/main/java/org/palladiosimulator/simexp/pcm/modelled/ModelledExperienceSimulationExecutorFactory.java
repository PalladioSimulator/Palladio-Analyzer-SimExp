package org.palladiosimulator.simexp.pcm.modelled;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public abstract class ModelledExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification>
        extends PcmExperienceSimulationExecutorFactory<R, V, T> {

    public ModelledExperienceSimulationExecutorFactory(IModelledWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore);
    }

    @Override
    protected IModelledWorkflowConfiguration getWorkflowConfiguration() {
        return (IModelledWorkflowConfiguration) super.getWorkflowConfiguration();
    }

    @Override
    protected final PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> doCreate(
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn) {
        return null;
    }

    @Override
    protected SimulationExecutor createLoaded(ModelLoader modelLoader, Experiment experiment,
            ProbabilisticModelRepository probabilisticModelRepository,
            DynamicBehaviourRepository dynamicBehaviourRepository) {
        URI smodelURI = getWorkflowConfiguration().getSmodelURI();
        ModelledModelLoader modelledModelLoader = (ModelledModelLoader) modelLoader;
        Smodel smodel = modelledModelLoader.loadSModel(smodelURI);

        DynamicBayesianNetwork<CategoricalValue> dbn = createDBN(probabilisticModelRepository,
                dynamicBehaviourRepository);
        return doModelledCreate(experiment, probabilisticModelRepository, dbn, smodel);
    }

    protected abstract PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> doModelledCreate(
            Experiment experiment, ProbabilisticModelRepository probabilisticModelRepository,
            DynamicBayesianNetwork<CategoricalValue> dbn, Smodel smodel);
}