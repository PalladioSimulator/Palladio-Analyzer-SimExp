package org.palladiosimulator.simexp.dsl.ea.launch.dispatcher;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDispatcherProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EAPreferenceConstants;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local.LocalEAFitnessEvaluator;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class LocalDispatcherProvider implements IDispatcherProvider {

    @Override
    public String getName() {
        return EAPreferenceConstants.DEFAULT_DISPATCHER;
    }

    @Override
    public IDisposeableEAFitnessEvaluator createEvaluator(IWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath) {
        IModelledWorkflowConfiguration modelledConfig = (IModelledWorkflowConfiguration) config;
        return new LocalEAFitnessEvaluator(modelledConfig, launchDescriptionProvider, seedProvider, modelLoaderFactory,
                resourcePath);
    }

}
