package org.palladiosimulator.simexp.pcm.modelled;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.config.IOptimizedConfiguration;
import org.palladiosimulator.simexp.pcm.modelled.config.impl.SimpleOptimizedConfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public abstract class ModelledExperienceSimulationExecutorFactory<R extends Number, V, T extends SimulatedMeasurementSpecification>
        extends PcmExperienceSimulationExecutorFactory<R, V, T> {

    public ModelledExperienceSimulationExecutorFactory(IModelledWorkflowConfiguration workflowConfiguration,
            ModelledModelLoader.Factory modelLoaderFactory,
            ISimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor,
                resourcePath);
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

    protected IOptimizedConfiguration getOptimizedConfiguration(IModelledWorkflowConfiguration config, Smodel smodel) {
        if (config instanceof IOptimizedConfiguration optimizedConfiguration) {
            return optimizedConfiguration;
        }

        Map<String, Object> optimizedValues = config.getOptimizedValues();
        if (optimizedValues != null) {
            return new OptimizedConfiguration(smodel, optimizedValues);
        }

        return new SimpleOptimizedConfiguration(smodel);
    }

}