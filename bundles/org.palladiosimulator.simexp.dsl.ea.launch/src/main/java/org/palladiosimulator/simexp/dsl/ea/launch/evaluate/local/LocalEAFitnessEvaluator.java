package org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.ISimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;
import org.palladiosimulator.simexp.workflow.launcher.SimulationExecutorLookup;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class LocalEAFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    protected static final Logger LOGGER = Logger.getLogger(LocalEAFitnessEvaluator.class);

    private final IModelledWorkflowConfiguration config;
    private final LaunchDescriptionProvider launchDescriptionProvider;
    private final Optional<ISeedProvider> seedProvider;
    private final Factory modelLoaderFactory;
    private final ExecutorService executor;

    public LocalEAFitnessEvaluator(IModelledWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory) {
        this.config = config;
        this.launchDescriptionProvider = launchDescriptionProvider;
        this.seedProvider = seedProvider;
        this.modelLoaderFactory = modelLoaderFactory;
        this.executor = Executors.newFixedThreadPool(1);
    }

    @Override
    public void dispose() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Future<Double> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        Future<Double> future = executor.submit(new Callable<Double>() {

            @Override
            public Double call() throws Exception {
                return doCalcFitness(optimizableValues);
            }
        });
        return future;
    }

    private Double doCalcFitness(List<OptimizableValue<?>> optimizableValues) throws CoreException {
        SimulationExecutorLookup simulationExecutorLookup = new SimulationExecutorLookup();
        SimulationExecutor effectiveSimulationExecutor = simulationExecutorLookup
            .lookupSimulationExecutor((ISimExpWorkflowConfiguration) config, launchDescriptionProvider, seedProvider);

        LOGGER.info("effective simulation start");
        try {
            effectiveSimulationExecutor.execute();
            effectiveSimulationExecutor.evaluate();
        } finally {
            effectiveSimulationExecutor.dispose();
        }
        LOGGER.info("effective simulation finish");

        // TODO:
        return 0.0;
    }
}