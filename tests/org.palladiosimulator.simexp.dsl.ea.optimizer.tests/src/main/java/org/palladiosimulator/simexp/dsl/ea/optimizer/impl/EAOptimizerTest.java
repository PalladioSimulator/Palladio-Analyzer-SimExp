package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.FitnessHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.util.RandomRegistry;

public class EAOptimizerTest {

    private static final double DELTA = 0.0001;

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private IEAOptimizer optimizer;

    @Mock
    private IEAConfig eaConfig;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    @Mock
    private IExpressionCalculator calculator;

    @Captor
    private ArgumentCaptor<List<OptimizableValue<?>>> optimizableListCaptor;

    private SmodelCreator smodelCreator;

    private ThreadLocal<Random> threadLocalRandom;

    private Function<? super Random, String> optFunction;

    private SetBoundsHelper setBoundsHelper;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);

        setBoundsHelper = new SetBoundsHelper();

        threadLocalRandom = ThreadLocal.withInitial(() -> {
            RandomRegistry.random(new Random(42));
            return new Random(42);
        });

        optFunction = r -> {
            optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver, 1);
            return "";
        };

        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        optimizer = eaOptimizer.create(eaConfig);
    }

    @Test
    public void simpleBooleanOptimizableSetTest() {
        SetBounds setBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.BOOL, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(optimizableListCaptor.capture(), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(50.0, capturedValues.get(capturedValues.size() - 1), DELTA);
        List<List<OptimizableValue<?>>> allValues = optimizableListCaptor.getAllValues();
        OptimizableValue<?> optimizableValue = allValues.get(0)
            .get(0);
        assertTrue((Boolean) optimizableValue.getValue());
    }

    @Test
    public void simpleDoubleOptimizableRangeTest() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0, 20.0,
                1.0);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(19.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void mediumDoubleOptimizableRangeTest() {
        double lowerBound = 0.0;
        double upperBound = 100.0;
        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, lowerBound,
                upperBound, 1.0);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99.0, capturedValues.get(0), DELTA);
        assertEquals(99.0, capturedValues.get(1), DELTA);
        assertEquals(99.0, capturedValues.get(2), DELTA);
        assertEquals(99.0, capturedValues.get(4), DELTA);
        assertEquals(99.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void largeDoubleOptimizableRangeTest() {
        double lowerBound = 0.0;
        double upperBound = 1000.0;
        double stepSize = 1.0;
        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, lowerBound,
                upperBound, stepSize);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {

            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(999.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void doubleOptimizableRangeTestWithNonNaturalNumbers() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.15, 10.0,
                0.5);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(9.15, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void simpleDoubleOptimizableSetTest() {
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0), calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(9.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void mediumDoubleOptimizableSetTest() {
        List<Double> listOfDoubles = List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999, 64.0,
                64.43, 99.99, 23.4, 45.4, 88.56, 93.22, 22.0, 19.34, 85.5);
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, listOfDoubles, calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99.999, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void largeDoubleOptimizableSetTest() {
        List<Double> listOfDoubles = List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999, 64.0,
                64.43, 99.99, 23.4, 45.4, 88.56, 93.22, 22.0, 19.34, 85.5, 68.0490, 13.9377, 22.8356, 62.6662, 80.6995,
                24.6583, 20.1058, 86.7853, 5.9211, 34.0451, 86.9018, 57.0348, 64.3853, 53.8212, 27.0908, 27.7072,
                50.1732, 13.7756, 15.3441, 63.3863, 45.0975, 42.4577, 0.6540, 50.5163, 12.5536, 7.2895, 59.4786,
                30.4763, 91.8267, 72.6185, 50.2461, 31.3506, 51.6624, 97.3466, 86.9306, 6.1069, 39.7333, 20.7608,
                62.6762, 98.8490, 32.1623, 36.0705, 26.6185, 82.6982, 75.1402, 6.4458, 74.1225, 46.4134, 61.4674,
                19.1120, 32.3976, 75.7141, 76.5742, 4.0075, 38.4529, 52.7934, 52.5135, 30.6208, 6.9900, 7.2939, 51.4043,
                28.4333, 32.9016, 33.4080, 57.7485, 71.6981, 77.3197, 86.2424, 91.7500, 51.6263, 9.8164, 67.7143,
                58.9597, 12.3699, 22.6720, 13.3816, 18.1779, 76.0132, 68.7411, 53.7471, 25.1162, 49.0425, 56.1356,
                48.7245, 57.4270, 34.4016, 66.3356, 11.9550, 69.6338, 60.0229, 93.9973, 43.7552, 30.0767, 55.4601,
                24.8844, 7.9797, 35.4908, 77.2986, 80.9350, 1.8039);
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, listOfDoubles, calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99.999, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void simpleIntegerOptimizableRangeTest() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 20, 1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(19.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void mediumIntegerOptimizableRangeTest() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 100, 1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void largeIntegerOptimizableRangeTest() {
        int lowerBound = 0;
        int upperBound = 100000;
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, lowerBound,
                upperBound, 1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99839.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void simpleIntegerOptimizableSetTest() {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 3, 7, 3, 8, 2, 9),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(9.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void mediumIntegerOptimizableSetTest() {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, 31, 84, 90, 40, 80, 28, 69, 74, 69, 29, 83, 31, 53, 35, 42, 80, 52, 85, 16), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(90.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void mediumIntegerOptimizableSetTestWithNegativeNumbers() {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, -31, 84, -90, 40, -80, 28, 69, 74, 69, 29, 83, -31, 53, 35, -42, 80, 52, 85, -16),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(85.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void largeIntegerOptimizableSetTest() {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(72, 57, 13, 25, 40, 8, 67, 49, 90, 58, 3, 27, 44, 91, 96, 39, 70, 20, 85, 78, 19, 34, 42, 95,
                        10, 87, 50, 4, 16, 61, 98, 88, 77, 23, 84, 63, 45, 33, 9, 56, 81, 18, 92, 31, 26, 5, 65, 60, 75,
                        80, 74, 36, 71, 97, 47, 1, 55, 43, 52, 2, 62, 21, 7, 48, 11, 32, 46, 73, 99, 79, 93, 64, 37, 29,
                        76, 53, 54, 17, 30, 28, 94, 22, 68, 14, 59, 24, 6, 35, 38, 89, 66, 41, 12, 15, 86, 82, 51, 83,
                        20, 0),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void integerDoubleOptimizableRangeTest() {
        int upperBoundInteger = 20;
        RangeBounds intRangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0,
                upperBoundInteger, 1);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, intRangeBound);
        double upperBoundDouble = 20.0;
        RangeBounds doubleRangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0,
                upperBoundDouble, 1.0);
        Optimizable doubleOptimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleRangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, doubleOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(38.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void integerDoubleOptimizableSetTest() {
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds doubleSetBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0), calculator);
        Optimizable doubleOptimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleSetBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, doubleOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(18.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void integerBooleanOptimizableSetTest() {
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds boolSetBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                calculator);
        Optimizable boolOptimizable = smodelCreator.createOptimizable("test", DataType.BOOL, boolSetBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, boolOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(59.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void integerBooleanDoubleOptimizableSetTest() {
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds boolSetBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                calculator);
        Optimizable boolOptimizable = smodelCreator.createOptimizable("test", DataType.BOOL, boolSetBound);
        SetBounds doubleSetBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0), calculator);
        Optimizable doubleOptimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleSetBound);
        when(optimizableProvider.getOptimizables())
            .thenReturn(List.of(intOptimizable, boolOptimizable, doubleOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(68.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void manySuboptimizablesOptimizableSetTest() {
        Random r = new Random(42);

        Map<Optimizable, Object> optimizables = new LinkedHashMap();
        for (int i = 0; i < 15; i++) {
            double randDouble = r.nextDouble();
            if (randDouble < 0.33) {
                // Integer
                List<Integer> numbers = new ArrayList();
                int max = 0;
                for (int upperBound = 0; upperBound < r.nextInt(1, 15); upperBound++) {
                    int nextInt = r.nextInt(100);
                    numbers.add(nextInt);
                    if (nextInt > max)
                        max = nextInt;
                }
                SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, numbers,
                        calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.INT, integerSetBound), max);
            } else if (randDouble < 0.66) {
                // Double
                List<Double> numbers = new ArrayList();
                double max = 0;
                for (int upperBound = 0; upperBound < r.nextInt(1, 15); upperBound++) {
                    double nextDouble = r.nextDouble(100);
                    numbers.add(nextDouble);
                    if (nextDouble > max)
                        max = nextDouble;
                }
                SetBounds doubleSetBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, numbers, calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleSetBound), max);
            } else {
                // Bool
                SetBounds boolSetBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                        calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.BOOL, boolSetBound), true);
            }

        }
        when(optimizableProvider.getOptimizables()).thenReturn(optimizables.keySet());
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        RandomRegistry.with(threadLocalRandom, optFunction);

        double maximumFitness = 0;
        for (Object obj : optimizables.values()) {
            if (obj instanceof Boolean) {
                maximumFitness += 50;
            } else if (obj instanceof Double doubleVal) {
                maximumFitness += doubleVal;
            } else {
                maximumFitness += (Integer) obj;
            }
        }
        LOGGER.info("Maximum Fitness: " + maximumFitness);
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(1041.84433, capturedValues.get(capturedValues.size() - 1), DELTA);
    }
}
