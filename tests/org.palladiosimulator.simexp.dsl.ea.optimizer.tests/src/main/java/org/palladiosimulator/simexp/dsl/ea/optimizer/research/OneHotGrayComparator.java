package org.palladiosimulator.simexp.dsl.ea.optimizer.research;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.util.RandomRegistry;

public class OneHotGrayComparator {

    private static final double DELTA = 0.00001;

    private final static Logger LOGGER = Logger.getLogger(OneHotGrayComparator.class);

    private final static int NUMBER_OF_RUNS = 50;

    @Mock
    private IExpressionCalculator calculator;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    private SmodelCreator smodelCreator;

    private SetBoundsHelper setBoundsHelper;

    private ThreadLocal<Random> threadLocalRandom;

    private Function<? super Random, String> grayOptFunction;

    public OneHotGrayComparator() {
        setBoundsHelper = new SetBoundsHelper();
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
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });
    }

    public static void main(String[] args) {
        OneHotGrayComparator oneHotGrayComparator = new OneHotGrayComparator();
        try {
            oneHotGrayComparator.tuneParameters();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void tuneParameters() throws InterruptedException {
        ConcurrentLinkedDeque<ParameterConfig> configWithValues = new ConcurrentLinkedDeque<>();
        ExecutorService executor = Executors.newFixedThreadPool(16);

        long counter = 0;

        for (double mutationRate = 0; Math.abs(mutationRate - 1) > DELTA; mutationRate += 0.1) {
            for (double crossoverRate = 0; Math.abs(crossoverRate - 1) > DELTA; crossoverRate += 0.1) {
                for (int selectorSize = 3; selectorSize <= 6; selectorSize++) {
                    for (int offspringSelSize = 3; offspringSelSize <= 6; offspringSelSize++) {
                        final int localSelectorSize = selectorSize;
                        final int localOffspringSelSize = offspringSelSize;
                        final double localMutationRate = mutationRate;
                        final double localCrossoverRate = crossoverRate;
                        final long currentExecution = counter++;

                        executor.submit(() -> {

                            EAOptimizerGrayEncoding grayOptimizer = new TuneableEAOptimizerGrayEncoding(500,
                                    new TournamentSelector(localSelectorSize),
                                    new TournamentSelector(localOffspringSelSize), new Mutator(localMutationRate),
                                    new UniformCrossover<>(localCrossoverRate));
                            grayOptFunction = r -> {
                                grayOptimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver, 1);
                                return "";
                            };

                            int n = 4;
                            Double avgFitness = 0.0;

                            for (int i = 0; i < n; i++) {
                                double bestFitness = runOptimization();
                                avgFitness += bestFitness / n;
                            }

                            configWithValues.add(new ParameterConfig(500, localSelectorSize, localOffspringSelSize,
                                    localMutationRate, localCrossoverRate, avgFitness));
                            LOGGER.info("Execution: " + currentExecution + " of " + (10 * 10 * 10 * 10));
                        });
                    }
                }
            }
        }
        executor.shutdown();

        executor.awaitTermination(5, TimeUnit.MINUTES);

        LOGGER.info("Overview: ");

        ParameterConfig bestConfig = configWithValues.getFirst();
        Double avgFitness = 0.0;
        for (ParameterConfig config : configWithValues) {
            LOGGER.info(config.toString());
            avgFitness += config.bestValue / configWithValues.size();
            if (config.bestValue > bestConfig.bestValue) {
                bestConfig = config;
            }
        }
        LOGGER.info("------------------");
        LOGGER.info(configWithValues.size() + " Runs. Best config: ");
        LOGGER.info(bestConfig.toString());
        LOGGER.info("Avg. Fitness");
        LOGGER.info(avgFitness);
    }

    public void runGrayOptimization() {
        EAOptimizerGrayEncoding grayOptimizer = new TuneableEAOptimizerGrayEncoding(500, new TournamentSelector(5),
                new TournamentSelector(5), new Mutator(0.2), new UniformCrossover<>(0.5));

        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0, 20.0,
                1.0);

        grayOptFunction = r -> {
            grayOptimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver, 1);
            return "";
        };

        List<Double> grayMaxFitnessValues = new ArrayList();

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            double bestFitness = runOptimization();
            grayMaxFitnessValues.add(bestFitness);
        }

        List<Double> oneHotMaxFitnessValues = new ArrayList();

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            double bestFitness = runOptimization();
            oneHotMaxFitnessValues.add(bestFitness);
        }

        StringBuilder strBuilder = new StringBuilder();
        int grayMaxFitnessReached = 0;
        for (Double fitness : grayMaxFitnessValues) {
            grayMaxFitnessReached += (Math.abs(fitness - 19.0) < DELTA) ? 1 : 0;
            strBuilder.append(fitness.toString() + "; ");
        }
        LOGGER.info("Gray: ");
        LOGGER.info(strBuilder);
        LOGGER.info("Max Fitness reached: " + grayMaxFitnessReached + " out of " + NUMBER_OF_RUNS + " times");

        strBuilder = new StringBuilder();
        int oneHotMaxFitnessReached = 0;
        for (Double fitness : oneHotMaxFitnessValues) {
            oneHotMaxFitnessReached += (Math.abs(fitness - 19.0) < DELTA) ? 1 : 0;
            strBuilder.append(fitness.toString() + "; ");
        }
        LOGGER.info("One Hot Encoding: ");
        LOGGER.info(strBuilder);
        LOGGER.info("Max Fitness reached: " + oneHotMaxFitnessReached + " out of " + NUMBER_OF_RUNS + " times");
    }

    private double runOptimization() {
        Random r = new Random();
        int newRandom = r.nextInt();

        threadLocalRandom = ThreadLocal.withInitial(() -> {
            RandomRegistry.random(new Random(newRandom));
            return new Random(newRandom);
        });

        RandomRegistry.with(threadLocalRandom, grayOptFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        return capturedValues.get(capturedValues.size() - 1);

    }

    private class ParameterConfig {

        private int populationSize;
        private int selector;
        private int offspringSelector;
        private double mutator;
        private double crossoverOperator;
        private double bestValue;

        public ParameterConfig(int populationSize, int selectorSize, int offspringSelectorSize, double mutationRate,
                double crossoverRate, double bestValue) {
            this.populationSize = populationSize;
            this.selector = selectorSize;
            this.offspringSelector = offspringSelectorSize;
            this.mutator = mutationRate;
            this.crossoverOperator = crossoverRate;
            this.bestValue = bestValue;
        }

        @Override
        public String toString() {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("Avg. Fitness: " + bestValue + " ||  ");
            strBuilder.append("PopSize: " + populationSize + "  ");
            strBuilder.append("Selector: " + selector + "  ");
            strBuilder.append("OffspringSel: " + offspringSelector + "  ");
            strBuilder.append("Mutation rate: " + mutator + "  ");
            strBuilder.append("Crossover rate: " + crossoverOperator + "  ");

            return strBuilder.toString();

        }
    }

}
