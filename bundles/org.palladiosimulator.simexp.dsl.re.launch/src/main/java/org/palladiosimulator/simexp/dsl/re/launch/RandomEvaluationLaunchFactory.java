package org.palladiosimulator.simexp.dsl.re.launch;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.dsl.dispatcher.DispatcherLookup;
import org.palladiosimulator.simexp.dsl.dispatcher.IDisposeableRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.preferences.DispatcherPreferenceConstants;
import org.palladiosimulator.simexp.dsl.dispatcher.util.IRewardFormater;
import org.palladiosimulator.simexp.dsl.dispatcher.util.RewardFormater;
import org.palladiosimulator.simexp.dsl.smodel.api.ISmodelConstants;
import org.palladiosimulator.simexp.dsl.smodel.api.PrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IRandomEvaluationWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class RandomEvaluationLaunchFactory implements ILaunchFactory {
    public static final int HANDLE_VALUE = 10;

    private final IPreferencesService preferencesService;

    public RandomEvaluationLaunchFactory() {
        this(Platform.getPreferencesService());
    }

    RandomEvaluationLaunchFactory(IPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.MODELLED) {
            return 0;
        }
        IModelledWorkflowConfiguration modelledWorkflowConfiguration = (IModelledWorkflowConfiguration) config;
        if (modelledWorkflowConfiguration.getOptimizationType() != ModelledOptimizationType.RANDOM) {
            return 0;
        }

        return HANDLE_VALUE;
    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config, String launcherName,
            SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory, ISimulatedExperienceAccessor accessor, Path resourcePath)
            throws CoreException {
        ModelLoader modelLoader = modelLoaderFactory.create();
        ModelledModelLoader modelledModelLoader = (ModelledModelLoader) modelLoader;
        IModelledWorkflowConfiguration modelledWorkflowConfiguration = (IModelledWorkflowConfiguration) config;
        URI smodelURI = modelledWorkflowConfiguration.getSmodelURI();
        Smodel smodel = modelledModelLoader.loadSModel(smodelURI);
        // TODO: get from SModel
        final int places = ISmodelConstants.PLACES;
        PrecisionProvider precisionProvider = new PrecisionProvider(places);
        RewardFormater rewardFormater = new RewardFormater(precisionProvider);
        IDisposeableRewardEvaluator fitnessEvaluator = createFitnessEvaluator(modelledWorkflowConfiguration,
                launcherName, description, seedProvider, modelLoaderFactory, rewardFormater, resourcePath);
        return new RandomEvaluationSimulationExecutor(smodel, fitnessEvaluator,
                (IRandomEvaluationWorkflowConfiguration) config, rewardFormater, seedProvider, resourcePath);
    }

    private IDisposeableRewardEvaluator createFitnessEvaluator(IModelledWorkflowConfiguration config,
            String launcherName, SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            ModelLoader.Factory modelLoaderFactory, IRewardFormater rewardFormater, Path resourcePath)
            throws CoreException {
        String dispatchername = preferencesService.getString(DispatcherPreferenceConstants.ID,
                DispatcherPreferenceConstants.DISPATCHER, "", null);
        DispatcherLookup dispatcherLookup = new DispatcherLookup();
        return dispatcherLookup.createEvaluator(dispatchername, config, launcherName, description, seedProvider,
                modelLoaderFactory, rewardFormater, resourcePath);
    }
}
