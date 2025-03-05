package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class KubernetesFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(KubernetesFitnessEvaluator.class);

    public KubernetesFitnessEvaluator(IModelledWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath) {
    }

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        LOGGER.info("dispatch fitness calculation");

        // TODO Auto-generated method stub
        return null;
    }

}
