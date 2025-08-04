package org.palladiosimulator.simexp.dsl.ea.launch.dispatcher;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDispatcherProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EAPreferenceConstants;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local.LocalEAFitnessEvaluator;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class LocalDispatcherProvider implements IDispatcherProvider {

    @Override
    public String getName() {
        return EAPreferenceConstants.DEFAULT_DISPATCHER;
    }

    @Override
    public IDisposeableEAFitnessEvaluator createEvaluator(IWorkflowConfiguration config, String launcherName,
            SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath) {
        IModelledWorkflowConfiguration modelledConfig = (IModelledWorkflowConfiguration) config;
        return new LocalEAFitnessEvaluator(modelledConfig, launcherName, description, seedProvider, modelLoaderFactory,
                resourcePath);
    }

}
