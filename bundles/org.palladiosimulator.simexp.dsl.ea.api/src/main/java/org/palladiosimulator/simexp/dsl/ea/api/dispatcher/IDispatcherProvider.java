package org.palladiosimulator.simexp.dsl.ea.api.dispatcher;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IDispatcherProvider {
    String getName();

    IDisposeableEAFitnessEvaluator createEvaluator(IWorkflowConfiguration config, String launcherName,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath);
}
