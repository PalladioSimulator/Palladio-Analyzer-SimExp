package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDispatcherProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class KubernetesDispatcherProvider implements IDispatcherProvider {

    @Override
    public String getName() {
        return "Kubernetes";
    }

    @Override
    public IDisposeableEAFitnessEvaluator createEvaluator(IWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath) {
        // TODO Auto-generated method stub
        return null;
    }
}
