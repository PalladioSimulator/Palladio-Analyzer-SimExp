package org.palladiosimulator.simexp.dsl.ea.launch.dispatcher;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluatorProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IDisposeableRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.preferences.DispatcherPreferenceConstants;
import org.palladiosimulator.simexp.dsl.dispatcher.util.IRewardFormater;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local.LocalEAFitnessEvaluator;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class LocalDispatcherProvider implements IRewardEvaluatorProvider {

    @Override
    public String getName() {
        return DispatcherPreferenceConstants.DEFAULT_DISPATCHER;
    }

    @Override
    public IDisposeableRewardEvaluator createEvaluator(IWorkflowConfiguration config, String launcherName,
            SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, IRewardFormater rewardFormater, Path resourcePath) {
        IModelledWorkflowConfiguration modelledConfig = (IModelledWorkflowConfiguration) config;
        return new LocalEAFitnessEvaluator(modelledConfig, launcherName, description, seedProvider, modelLoaderFactory,
                resourcePath);
    }

}
