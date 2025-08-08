package org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.store.csv.accessor.CsvAccessor;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.ea.launch.EAOptimizerLaunchFactory;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.launcher.SimulationExecutorLookup;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class LocalEAFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(LocalEAFitnessEvaluator.class);

    private final IModelledWorkflowConfiguration config;
    private final String launcherName;
    private final SimulatedExperienceStoreDescription description;
    private final Optional<ISeedProvider> seedProvider;
    private final ExecutorService executor;
    private final Path resourcePath;
    private final ClassLoader classloader;

    private int counter = 0;

    public LocalEAFitnessEvaluator(IModelledWorkflowConfiguration config, String launcherName,
            SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath) {
        this.config = config;
        this.launcherName = launcherName;
        this.description = description;
        this.seedProvider = seedProvider;
        this.executor = Executors.newFixedThreadPool(1,
                new BasicThreadFactory.Builder().namingPattern("local-ea-thread-%d")
                    .build());
        this.resourcePath = resourcePath;
        this.classloader = Thread.currentThread()
            .getContextClassLoader();
    }

    @Override
    public int getParallelism() {
        return 1;
    }

    @Override
    public IQualityAttributeProvider getQualityAttributeProvider() {
        // TODO:
        return null;
    }

    @Override
    public void evaluate(EvaluatorClient evaluatorClient) {
        try {
            evaluatorClient.process(this);
        } finally {
            dispose();
        }
    }

    private void dispose() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public synchronized Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        int currentCounter = counter++;
        Future<Optional<Double>> future = executor.submit(new Callable<>() {

            @Override
            public Optional<Double> call() throws Exception {
                /**
                 * Treats this thread as belonging to the simulation with respect to logging. See:
                 * de.uka.ipd.sdq.workflow.logging.console.StreamsProxyAppender
                 */
                Thread.currentThread()
                    .setContextClassLoader(classloader);
                return doCalcFitness(currentCounter, optimizableValues);
            }
        });
        return future;
    }

    private Optional<Double> doCalcFitness(int counter, List<OptimizableValue<?>> optimizableValues)
            throws CoreException {
        SimulationExecutorLookup simulationExecutorLookup = new SimulationExecutorLookup(
                EAOptimizerLaunchFactory.HANDLE_VALUE);

        OptimizableSimExpWorkflowConfiguration optimizableSimExpWorkflowConfiguration = new OptimizableSimExpWorkflowConfiguration(
                (SimExpWorkflowConfiguration) config, optimizableValues);

        Path currentResourceFolder = resourcePath.resolve(String.format("ea_%04d", counter));
        try {
            Files.createDirectories(currentResourceFolder);
        } catch (IOException e) {
            IStatus status = new Status(IStatus.ERROR, "org.palladiosimulator.simexp.dsl.ea.launch", 0, e.getMessage(),
                    e);
            throw new CoreException(status);
        }
        ISimulatedExperienceAccessor accessor = new CsvAccessor(currentResourceFolder);
        SimulationExecutor effectiveSimulationExecutor = simulationExecutorLookup.lookupSimulationExecutor(
                optimizableSimExpWorkflowConfiguration, launcherName, description, seedProvider, accessor,
                currentResourceFolder);

        LOGGER.info(String.format("### fitness evaluation start: %d ###", counter));
        OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
        LOGGER.info(String.format("evaluate: %s", optimizableValueToString.asString(optimizableValues)));
        ISimulationResult simulationResult = execute(effectiveSimulationExecutor);
        LOGGER.info(String.format("### fitness evaluation finished: %d reward = %s ###", counter,
                simulationResult.getTotalReward()));
        return Optional.of(simulationResult.getTotalReward());
    }

    private ISimulationResult execute(SimulationExecutor effectiveSimulationExecutor) {
        try {
            try {
                effectiveSimulationExecutor.execute();
                return effectiveSimulationExecutor.evaluate();
            } finally {
                effectiveSimulationExecutor.dispose();
            }
        } finally {
            RepositoryManager.clearRepositories(RepositoryManager.getCentralRepository());
        }
    }
}
