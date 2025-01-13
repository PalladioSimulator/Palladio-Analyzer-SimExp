package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local.LocalEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class EAOptimizerLaunchFactory implements ILaunchFactory {
    public static final int HANDLE_VALUE = 10;

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.MODELLED) {
            return 0;
        }
        IModelledWorkflowConfiguration modelledWorkflowConfiguration = (IModelledWorkflowConfiguration) config;
        if (modelledWorkflowConfiguration.getOptimizationType() != ModelledOptimizationType.EVOLUTIONARY_ALGORITHM) {
            return 0;
        }

        return HANDLE_VALUE;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory) {
        ModelLoader modelLoader = modelLoaderFactory.create();
        ModelledModelLoader modelledModelLoader = (ModelledModelLoader) modelLoader;
        IModelledWorkflowConfiguration modelledWorkflowConfiguration = (IModelledWorkflowConfiguration) config;
        URI smodelURI = modelledWorkflowConfiguration.getSmodelURI();
        Smodel smodel = modelledModelLoader.loadSModel(smodelURI);
        IDisposeableEAFitnessEvaluator fitnessEvaluator = createFitnessEvaluator(modelledWorkflowConfiguration,
                launchDescriptionProvider, seedProvider, modelLoaderFactory);
        return new EAOptimizerSimulationExecutor(smodel, fitnessEvaluator);
    }

    private IDisposeableEAFitnessEvaluator createFitnessEvaluator(IModelledWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory) {
        return new LocalEAFitnessEvaluator(config, launchDescriptionProvider, seedProvider, modelLoaderFactory);
    }

}
