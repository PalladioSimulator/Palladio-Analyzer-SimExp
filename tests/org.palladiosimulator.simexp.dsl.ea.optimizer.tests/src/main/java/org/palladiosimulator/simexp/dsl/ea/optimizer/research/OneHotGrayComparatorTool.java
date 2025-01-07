package org.palladiosimulator.simexp.dsl.ea.optimizer.research;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.TuneableEAOptimizerOneHotEncoding;
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

public class OneHotGrayComparatorTool {

    private final static int NUMBER_OF_RUNS = 200;

    private static final double DELTA = 0.00001;

    private final static Logger LOGGER = Logger.getLogger(OneHotGrayComparatorTool.class);

    @Mock
    private IExpressionCalculator calculator;
    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IOptimizableProvider optimizableProvider;

    private SmodelCreator smodelCreator;

    public OneHotGrayComparatorTool() {
        smodelCreator = new SmodelCreator();
        initMocks(this);

        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0, 100000.0,
                1.0);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        setUpMockFunctions(optimizable);
    }

    public static void main(String[] args) {
        OneHotGrayComparatorTool oneHotGrayComparatorTool = new OneHotGrayComparatorTool();
        oneHotGrayComparatorTool.runGrayOptimization();
    }

    public void runGrayOptimization() {
        LOGGER.info("Start");
        IEAFitnessEvaluator grayFitnessEvaluator = mock(IEAFitnessEvaluator.class);
        IEAFitnessEvaluator ohFitnessEvaluator = mock(IEAFitnessEvaluator.class);

        AtomicInteger numFitnessEvalsGray = new AtomicInteger(0);
        AtomicInteger numFitEvaluationOH = new AtomicInteger(0);
        final FitnessHelper fitnessHelper = new FitnessHelper();

        when(grayFitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                numFitnessEvalsGray.incrementAndGet();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });
        when(ohFitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                numFitEvaluationOH.incrementAndGet();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAOptimizerGrayEncoding grayOptimizer = new TuneableEAOptimizerGrayEncoding(100, new TournamentSelector(7),
                new TournamentSelector(11), new Mutator(0.9), new UniformCrossover<>(0.3));

        TuneableEAOptimizerOneHotEncoding tuneableEAOptimizerOneHotEncoding = new TuneableEAOptimizerOneHotEncoding(100,
                new TournamentSelector<>(7), new TournamentSelector<>(3), new Mutator<>(0.0007),
                new UniformCrossover<>(0.3));

        Function<? super Random, String> grayOptFunction = r -> {
            grayOptimizer.optimize(optimizableProvider, grayFitnessEvaluator, statusReceiver, 1);
            return "";
        };

        Function<? super Random, String> ohOptFunction = r -> {
            tuneableEAOptimizerOneHotEncoding.optimize(optimizableProvider, ohFitnessEvaluator, statusReceiver, 1);
            return "";
        };

        List<Double> grayMaxFitnessValues = new ArrayList();
        List<Double> oneHotMaxFitnessValues = new ArrayList();
        long grayRuntime = 0;
        long ohRuntime = 0;

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            Random r = new Random();
            int newRandom = r.nextInt();

            long timeBeforeRun = System.nanoTime();
//            double grayBestFitness = runOptimization(newRandom, grayOptFunction, statusReceiver);
//            grayMaxFitnessValues.add(grayBestFitness);
            grayRuntime += System.nanoTime() - timeBeforeRun;

            long ohTimeBeforeRun = System.nanoTime();
            double ohBestFitness = runOptimization(newRandom, ohOptFunction, statusReceiver);
            oneHotMaxFitnessValues.add(ohBestFitness);
            ohRuntime += System.nanoTime() - ohTimeBeforeRun;
            LOGGER.info("Run: " + i);
        }

        StringBuilder strBuilder = new StringBuilder();
        int grayMaxFitnessReached = 0;
        double grayAvg = 0.0;
        for (Double fitness : grayMaxFitnessValues) {
            grayMaxFitnessReached += (Math.abs(fitness - 19.0) < DELTA) ? 1 : 0;
            grayAvg += fitness / grayMaxFitnessValues.size();
            strBuilder.append(fitness.toString() + "; ");
        }
        LOGGER.info("Gray: ");
        LOGGER.info(strBuilder);
        LOGGER.info("Max Fitness reached: " + grayMaxFitnessReached + " out of " + NUMBER_OF_RUNS + " times");
        LOGGER.info("Gray Avg: " + grayAvg);
        LOGGER.info("FitnessEvals Gray: " + numFitnessEvalsGray.get() + " Runtime: " + grayRuntime);
        LOGGER.info("True FitnessEvals Gray: " + FitnessHelper.activated.get());

        strBuilder = new StringBuilder();
        int oneHotMaxFitnessReached = 0;
        double ohAvg = 0;
        for (Double fitness : oneHotMaxFitnessValues) {
            oneHotMaxFitnessReached += (Math.abs(fitness - 19.0) < DELTA) ? 1 : 0;
            ohAvg += fitness / oneHotMaxFitnessValues.size();
            strBuilder.append(fitness.toString() + "; ");
        }
        LOGGER.info("One Hot Encoding: ");
        LOGGER.info(strBuilder);
        LOGGER.info("Max Fitness reached: " + oneHotMaxFitnessReached + " out of " + NUMBER_OF_RUNS + " times");
        LOGGER.info("OH Avg: " + ohAvg);
        LOGGER.info("Fitnessvals OH: " + numFitEvaluationOH.get() + " Runtime: " + ohRuntime);
    }

    private double runOptimization(long seed, Function<? super Random, String> optFunction,
            IEAEvolutionStatusReceiver statusReceiver) {

        ThreadLocal<Random> threadLocalRandom = ThreadLocal.withInitial(() -> {
            RandomRegistry.random(new Random(seed));
            return new Random(seed);
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        return capturedValues.get(capturedValues.size() - 1);

    }

    private void setUpMockFunctions(Optimizable optimizable) {
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));  
    }
}
