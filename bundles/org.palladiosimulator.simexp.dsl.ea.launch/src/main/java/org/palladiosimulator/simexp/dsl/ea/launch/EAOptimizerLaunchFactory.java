package org.palladiosimulator.simexp.dsl.ea.launch;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.DispatcherLookup;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EAPreferenceConstants;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.CachingEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmWorkflowConfiguration;
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

    private final IPreferencesService preferencesService;

    public EAOptimizerLaunchFactory() {
        this(Platform.getPreferencesService());
    }

    EAOptimizerLaunchFactory(IPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

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
            ModelLoader.Factory modelLoaderFactory, SimulatedExperienceAccessor accessor, Path resourcePath)
            throws CoreException {
        ModelLoader modelLoader = modelLoaderFactory.create();
        ModelledModelLoader modelledModelLoader = (ModelledModelLoader) modelLoader;
        IModelledWorkflowConfiguration modelledWorkflowConfiguration = (IModelledWorkflowConfiguration) config;
        URI smodelURI = modelledWorkflowConfiguration.getSmodelURI();
        Smodel smodel = modelledModelLoader.loadSModel(smodelURI);
        IDisposeableEAFitnessEvaluator fitnessEvaluator = createFitnessEvaluator(modelledWorkflowConfiguration,
                launchDescriptionProvider, seedProvider, modelLoaderFactory, resourcePath);
        fitnessEvaluator = new CachingEAFitnessEvaluator(fitnessEvaluator);
        return new EAOptimizerSimulationExecutor(smodel, fitnessEvaluator,
                (IEvolutionaryAlgorithmWorkflowConfiguration) config);
    }

    private IDisposeableEAFitnessEvaluator createFitnessEvaluator(IModelledWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath) throws CoreException {
        String dispatchername = preferencesService.getString(EAPreferenceConstants.ID, EAPreferenceConstants.DISPATCHER,
                "", null);
        DispatcherLookup dispatcherLookup = new DispatcherLookup();
        return dispatcherLookup.createEvaluator(dispatchername, config, launchDescriptionProvider, seedProvider,
                modelLoaderFactory, resourcePath);
    }
}
