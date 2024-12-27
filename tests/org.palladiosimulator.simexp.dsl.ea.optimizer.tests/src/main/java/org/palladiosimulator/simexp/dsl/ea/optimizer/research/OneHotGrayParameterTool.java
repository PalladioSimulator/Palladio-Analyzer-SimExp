package org.palladiosimulator.simexp.dsl.ea.optimizer.research;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizerGrayEncoding;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.TuneableEAOptimizerGrayEncoding;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.FitnessHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.util.RandomRegistry;

public class OneHotGrayParameterTool {

    private static final int POPULATION_SIZE = 100;

    private static final double DELTA = 0.00001;

    private final static Logger LOGGER = Logger.getLogger(OneHotGrayParameterTool.class);

    @Mock
    private IExpressionCalculator calculator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    private SmodelCreator smodelCreator;

    public OneHotGrayParameterTool() {
        smodelCreator = new SmodelCreator();
        initMocks(this);

        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0, 1000.0,
                1.0);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        setUpMockFunctions(optimizable);
    }

    private void setUpMockFunctions(Optimizable optimizable) {
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));  
    }

    public static void main(String[] args) {
        OneHotGrayParameterTool oneHotGrayComparator = new OneHotGrayParameterTool();
        try {
            oneHotGrayComparator.tuneParameters();
        } catch (InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void tuneParameters() throws InterruptedException, IOException {
        ConcurrentLinkedDeque<ParameterConfig> configWithValues = new ConcurrentLinkedDeque<>();
        ExecutorService executor = Executors.newFixedThreadPool(8);

        long counter = 0;
        long currentTimeMillis = System.currentTimeMillis();
        IEAFitnessEvaluator fitnessEvaluator = mock(IEAFitnessEvaluator.class);
        final ConcurrentHashMap<Long, Integer> numFitnessEvals = new ConcurrentHashMap<>();
        final FitnessHelper fitnessHelper = new FitnessHelper();

        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                numFitnessEvals.compute(Thread.currentThread()
                    .getId(), (key, value) -> value + 1);
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        for (double mutationRate = 0; Math.abs(mutationRate - 1.0) > DELTA; mutationRate += 0.1) {
            for (double crossoverRate = 0; Math.abs(crossoverRate - 1.0) > DELTA; crossoverRate += 0.1) {
                for (int selectorSize = 11; selectorSize <= 11; selectorSize += 4) {
                    for (int offspringSelSize = 11; offspringSelSize <= 11; offspringSelSize += 4) {
                        final int localSelectorSize = selectorSize;
                        final int localOffspringSelSize = offspringSelSize;
                        final double localMutationRate = mutationRate;
                        final double localCrossoverRate = crossoverRate;
                        final long currentExecution = counter++;

                        executor.submit(() -> {

                            numFitnessEvals.put(Thread.currentThread()
                                .getId(), 0);

                            EAOptimizerGrayEncoding optimizer = new TuneableEAOptimizerGrayEncoding(POPULATION_SIZE,
                                    new TournamentSelector(localSelectorSize),
                                    new TournamentSelector(localOffspringSelSize), new Mutator(localMutationRate),
                                    new UniformCrossover<>(localCrossoverRate));

                            IEAEvolutionStatusReceiver statusReceiver = mock(IEAEvolutionStatusReceiver.class);

                            Function<? super Random, String> optFunction = r -> {
                                optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver, 1);
                                return "";
                            };

                            int n = 20;
                            Double avgFitness = 0.0;

                            for (int i = 0; i < n; i++) {
                                double bestFitness = runOptimization(optFunction, statusReceiver);
                                avgFitness += bestFitness / n;
                            }

                            configWithValues.add(new ParameterConfig(POPULATION_SIZE, localSelectorSize,
                                    localOffspringSelSize, localMutationRate, localCrossoverRate, avgFitness,
                                    numFitnessEvals.get(Thread.currentThread()
                                        .getId())));
                            LOGGER.info("Execution: " + currentExecution + " of " + (10 * 10 * 10 * 10));
                        });
                    }
                }
            }
        }
        executor.shutdown();

        executor.awaitTermination(400, TimeUnit.MINUTES);

        LOGGER.info("Overview: ");

        ParameterConfig bestConfig = configWithValues.getFirst();
        Double avgFitness = 0.0;
        for (ParameterConfig config : configWithValues) {
            LOGGER.info(config.toString());
            avgFitness += config.getBestValue() / configWithValues.size();
            if (config.getBestValue() > bestConfig.getBestValue()) {
                bestConfig = config;
            }
        }
        LOGGER.info("------------------");
        LOGGER.info(configWithValues.size() + " Runs. Best config: ");
        LOGGER.info(bestConfig.toString());
        LOGGER.info("Avg. Fitness");
        LOGGER.info(avgFitness);
        LOGGER.info("Runtime: " + ((System.currentTimeMillis() - currentTimeMillis) / 1000) + " s");

        ParameterOptimizationVisualizer.exportToJSON(configWithValues);
    }

    private double runOptimization(Function<? super Random, String> optFunction,
            IEAEvolutionStatusReceiver statusReceiver) {
        Random r = new Random();
        int newRandom = r.nextInt();

        ThreadLocal<Random> threadLocalRandom = ThreadLocal.withInitial(() -> {
            RandomRegistry.random(new Random(newRandom));
            return new Random(newRandom);
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        return capturedValues.get(capturedValues.size() - 1);

    }

}
