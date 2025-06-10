package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.ConfigHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.FitnessHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.util.RandomRegistry;

public class EAOptimizerTest {

    private static final double DELTA = 0.0001;

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private EAOptimizer optimizer;

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

    private Function<? super Random, EAResult> optFunction;

    private SetBoundsHelper setBoundsHelper;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);
        when(calculator.getEpsilon()).thenReturn(DELTA);

        setBoundsHelper = new SetBoundsHelper();

        threadLocalRandom = ThreadLocal.withInitial(() -> {
            RandomRegistry.random(new Random(42));
            return new Random(42);
        });

        optFunction = r -> {
            return optimizer.optimizeSingleThread(optimizableProvider, fitnessEvaluator, statusReceiver);
        };
        EAOptimizerFactory eaOptimizerFactory = new EAOptimizerFactory();
        eaConfig = new ConfigHelper(80, 0.01, 0.8, 7, 100);
        optimizer = (EAOptimizer) eaOptimizerFactory.create(eaConfig);
    }

    @Test
    public void simpleBooleanOptimizableSetTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.BOOL, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult eaResult = RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(Long.class), optimizableListCaptor.capture(),
                captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(50.0, capturedValues.get(capturedValues.size() - 1), DELTA);
        List<List<OptimizableValue<?>>> allValues = optimizableListCaptor.getAllValues();
        OptimizableValue<?> optimizableValue = allValues.get(0)
            .get(0);
        assertTrue((Boolean) optimizableValue.getValue());
        assertEquals(50.0, eaResult.getFitness(), DELTA);
        OptimizableValue<?> finalOptimizableValue = eaResult.getOptimizableValuesList()
            .get(0)
            .get(0);
        assertEquals(optimizable, finalOptimizableValue.getOptimizable());
        assertEquals(true, finalOptimizableValue.getValue());
    }

    @Test
    public void mediumDoubleOptimizableRangeTest() throws IOException {
        DoubleLiteral lowerBound = smodelCreator.createDoubleLiteral(0.0);
        DoubleLiteral upperBound = smodelCreator.createDoubleLiteral(100.0);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1.0);
        when(calculator.calculateDouble(lowerBound)).thenReturn(lowerBound.getValue());
        when(calculator.calculateDouble(upperBound)).thenReturn(upperBound.getValue());
        when(calculator.calculateDouble(step)).thenReturn(step.getValue());
        RangeBounds rangeBound = smodelCreator.createRangeBoundsClosedOpen(lowerBound, upperBound, step);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(99.0, result.getFitness(), DELTA);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                0.00001);
    }

    @Test
    public void largeDoubleOptimizableRangeTest() throws IOException {
        DoubleLiteral lowerBoundDouble = smodelCreator.createDoubleLiteral(0.0);
        DoubleLiteral upperBoundDouble = smodelCreator.createDoubleLiteral(1000.0);
        DoubleLiteral stepDouble = smodelCreator.createDoubleLiteral(1.0);
        when(calculator.calculateDouble(lowerBoundDouble)).thenReturn(lowerBoundDouble.getValue());
        when(calculator.calculateDouble(upperBoundDouble)).thenReturn(upperBoundDouble.getValue());
        when(calculator.calculateDouble(stepDouble)).thenReturn(stepDouble.getValue());
        RangeBounds rangeBound = smodelCreator.createRangeBoundsClosedOpen(lowerBoundDouble, upperBoundDouble,
                stepDouble);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(991.0, result.getFitness(), DELTA);
        assertEquals(1, result.getOptimizableValuesList()
            .size());
        OptimizableValue<?> optimizableValue = result.getOptimizableValuesList()
            .get(0)
            .get(0);
        assertEquals(optimizable, optimizableValue.getOptimizable());
        assertEquals(991.0, optimizableValue.getValue());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void doubleOptimizableRangeTestWithNonNaturalNumbers() throws IOException {
        RangeBounds rangeBound = new RangeBoundsHelper().initializeDoubleRangeBound(smodelCreator, calculator, 0.15,
                10.0, 0.5);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(9.65, result.getFitness(), 0.00001);
        OptimizableValue<?> optimizableValue = result.getOptimizableValuesList()
            .get(0)
            .get(0);
        assertEquals(optimizable, optimizableValue.getOptimizable());
        assertEquals(9.65, optimizableValue.getValue());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void simpleDoubleOptimizableSetTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 9.0, 9.0), calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(3, result.getOptimizableValuesList()
            .size());
        assertEquals(9.0, result.getFitness(), 0.00001);
        List<List<OptimizableValue<?>>> optimizableValuesList = result.getOptimizableValuesList();
        assertEquals(optimizable, optimizableValuesList.get(0)
            .get(0)
            .getOptimizable());
        assertEquals(9.0, optimizableValuesList.get(0)
            .get(0)
            .getValue());
        assertEquals(9.0, optimizableValuesList.get(1)
            .get(0)
            .getValue());
        assertEquals(9.0, optimizableValuesList.get(2)
            .get(0)
            .getValue());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                0.00001);
    }

    @Test
    public void mediumDoubleOptimizableSetTest() throws IOException {
        List<Double> listOfDoubles = List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999, 64.0,
                64.43, 99.99, 23.4, 45.4, 88.56, 93.22, 22.0, 19.34, 85.5);
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, listOfDoubles, calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(99.999, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void largeDoubleOptimizableSetTest() throws IOException {
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
        double estimatedOptimumFitness = 99.999;
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, listOfDoubles, calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(99.99, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void simpleStringOptimizableSetTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeStringSetBound(smodelCreator,
                List.of("Hello", "World", "!", "How", "are", "youuuu", "abcdef"), calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.STRING, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(2, result.getOptimizableValuesList()
            .size());
        assertEquals(6.0, result.getFitness(), DELTA);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        assertEquals("abcdef", result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getValue());
        assertEquals("youuuu", result.getOptimizableValuesList()
            .get(1)
            .get(0)
            .getValue());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), optimizableListCaptor.capture(),
                captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                0.00001);
        List<String> expectedBestGenotypesFitnessEvolution = List.of("abcdef", "youuuu", "youuuu", "youuuu", "youuuu",
                "abcdef", "abcdef");
        assertArrayEquals(expectedBestGenotypesFitnessEvolution.stream()
            .toArray(),
                optimizableListCaptor.getAllValues()
                    .stream()
                    .map((List<OptimizableValue<?>> l) -> l.get(0)
                        .getValue())
                    .toArray());
    }

    @Test
    public void mediumStringOptimizableSetTest() throws IOException {
        List<String> listOfDoubles = List.of("Nq06", "6xmPSfJtRKU0", "", "LZ4JyxP", "3TA", "AOEf1JPoQyVStrg5YK", "dm",
                "bdxtTmlypfap", "qT0wClBAC5zjgv0A4rTq", "nbwCQhv", "5beIQ16f0Vu7t", "ycqRYzeeDcBLaM3", "BOL1ngaNk",
                "vuhHk", "7b1m7oM84ujmmpoG", "zXiKgr8f13", "pzxf9iEPA3qocBiEzo", "0A78Gr", "Ru8wa7c2mv4f",
                "wRdUn3t4FLUGx2F8j");
        SetBounds setBound = setBoundsHelper.initializeStringSetBound(smodelCreator, listOfDoubles, calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.STRING, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(20.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void largeStringOptimizableSetTest() throws IOException {
        List<String> listOfDoubles = List.of("aOHxv9wds0Rq4mnXHlmX46", "W7PhffqBEBditr",
                "KfKtcRnBQyIIXb4LTOh1c3pmm8nmzND6L7tkhu", "EKuKumON10ZKSX7zxOi",
                "0F4DUScGfteSJuJY2gw7L1RQ0Xh7uL5W9uT9cUDX", "kkF5nrs4XJEsoMBUGm6A", "Fs0jR6BWoKsSNre1cx2PqVHDu",
                "V4nZGw0KZflYSWCN4rs", "ramXMDt6TXjyP0zH", "nJTt", "4sKq", "5D988NCE8",
                "wGczZGePCza1Ef9SKWuDXwwf4NbAPjo", "gmnyGD1", "LvN", "yCef6owUPban", "Q6tmizXXBB1mJXeLHGP7pEyIIInR",
                "LBU5ovSV", "bSC6RU5JPypQp3XgOqAmSl", "PpTDH7Q1IwTpa1euZ4Pfc4Wxm7",
                "BYgma5dRB7IQBerSlmyxGHAKx5eN03sTXh5h", "aWkMf2WTbGLrHq17GP6g5",
                "KXWD1o5tZhwep4Nf1mC1AhqssNci8UW4qthYqqrq", "bCk", "wzZE471E3KiSfNiLY",
                "QLM3qRLWwNPlzDjL2VM0OQPQxeeKLeHZsSt", "pfxukreGYgQmzq2Xa7waZ1K2TyxFpw",
                "siMROpUeCY48eRNQT6EdraWr8CD3ATRSOsv1", "FHe6HVumNGXjC", "bzyYm80K8lYJin3Hl",
                "LBR5gbJPduhC17Rn4pjNbcP5WbKgviA55", "66ghmXg9izq", "akyMaeNJkESUvdDr6zPv",
                "3VhKVolwgqj7YqrfMYzObPhLLTwGaDlSzw8DH6", "K3QjyZFJPMGLn2VUx057R6Zs49T7xZsJ",
                "vyP5HWmp7TShMkvWxxByZAlAswt37rmgxMF2", "uCHf3GeRPtpqFBxe2dIybTwDfp4UY",
                "ZvaUxEflsBMxsmamInzfvUH55804zrX5nhnosvD", "cPBQn08jiu", "PMo3Cp", "hd", "FgkQv3SODLAlPXL",
                "wv1eyPDGZgirCt85ekZnmCQF", "wtm69M79gktAPZJhW4mnIdYMa", "9yXTVql1vFao1WexKISBj6F",
                "LZyWQmNd75JcahrkpoqLT", "SUJ0vmJZeyGPyz21f", "9rYrl3qbfXfewhQUrRILOybrTP7W8VB",
                "dbOvOSmf2xHIlNtqiZRYP1CFB10A8PgLq", "h6pMzbX7tuyk3hQYhfIRtzjjfqUJD8a", "P5sT5pZOGTEtQxEl9o5J7eX6Rmy3",
                "Ma62yJLDlUEbfW53FtZUOeKJAodKTdb", "f3W3ZmPbRrxbTr", "sdcHSJhyu1tw3k0XNHKtut2Hp0oXNqPx",
                "LsiQJDweglvcNOfyljcKBzVvMDMDHDv0rUhO6", "sf75JRH", "rWsBPkxpSvx9Xw", "dhxJFGmWELXIvfD9g1H",
                "SpVwrjSPe4EEK4M8YswLeqozoEmeNmc", "KoizZzkfS2zPSKE6DFzEbm1Gv6GxPk8mb1pJz", "Cht6gPj4vwuPNCFL5",
                "4xrJ4kLRA63W6Qmy", "I2Sj", "MAAmzjEXxU", "AFfl", "2QKm8NNQ7A2gSF7PVk", "r5Qi4h4LpRUKWB3",
                "XD9cA3spcKWqv8", "rq3bu1UNwnEJQw139PPDg07mg9d69fCyq", "IcqFkcbl90s0vE6EvAoSbwr",
                "FmI6qrKwC7g4n4q4HwnOUI", "u3n25BPlN01NHQPaiSkk", "BVgem25R", "3QfNnNXbFymI37sGet1oyWBGLd", "RrsYlJbTH",
                "FRxiyxHmxyvkb", "KT6spsagZupRyhQbfkFs8VNEkwK6", "mjebXsggybwqj", "kDNkSIzTnUt2qBjkUw7DchEA9tljA",
                "cDY12MzsgPsuQy", "AL", "wVbsXiMSQPPOsefGZndnXsiCUqyB8nvXGjrPVix", "eEYu7mIooTl7IUPsZ0u360ark",
                "Hrzcl6Fl6nahC7uNZfZSaeZJ13krwelK", "aBjp4q0eAWJtbi0GUeR71kS", "ykp1EZ2K1sS4OnpUHE429",
                "OTdwNAj3huhSDwUG", "WCbJatvWft", "Ol72PRjTEjn5HwF42hIB1xGmw", "JaDtjg5U8LrqjbJbraLh6Tij1oZbHGWarh8j",
                "GREcL2kZieDv54ZMNO1XOshgbAczrZDC4x7QzHI", "6dmVilE8AGOF1A3TfUyRc851mLTi98LHBM52b",
                "IJL7EJoTPSGUP2gmAhYmDD6HGtl6eIJmsqD", "y0c07YCbv8yzvPqUqlFmqHQ8JA4LO", "c9Sb3SdlVyp0CZtICrqk",
                "1dR8H7RDUZgprr3IHfwb7Yyj8GE", "H", "URnxurCEz35rx", "bIxJ6HiVQKxsKRnKHEgf",
                "xdLcMdhgam3gBDzvI2r8GtMVE61", "e88GaEA", "3DLah0hCL9w5AWD", "MeR",
                "R6EaPmdzksyvpiPhaADbBDMzVXL1beGBfkSVs4", "O", "SKtcR9jdhP3NCU2rfRax47Q1Ifo9nxOmuZ2a",
                "a0lWvuaTb2aTLAlajeSZI", "difopr8JCsRxDaDUzlQ7b9bQWX4AALVjzLJIPgfJ", "IZJEofhHdXfM80ZbEAUn0ggDq4",
                "F6zRg3K2Gwi0cmswK7C", "2hDcIA8T5kyym", "c", "wZIo26APfPulobklnTWxUQcCJ4EGx5Rr3tw98", "w16uyaSm",
                "ssbsoFWqeshe8eUeN3m0bLnsDP0SODez8", "G97ptME0dohuouxyeKVyD", "tij0vTvW", "fHh8hqpBiJ2AlFyCPQ",
                "wVuvuOyW46YHbyzU44sUV7iBt8dYBangUdfS", "Q1m383WCMu5SKK9s", "cdJVYYDU", "XeDV3haZ",
                "w2Dxekt5E8VjMgZ066TSqsq", "VOFGHXU8joHuhz2ZDkPJ1", "ZGF2E", "d0soyKIHWJS4J2OdTFgUTyLELy9E8k6", "ld",
                "SjNAoCxtXBDXLIwmjLY", "nkhAFCj7Ay4LodWsIm", "jSThfD", "66OwlEHcT7N4wj01xJIYQQvlltIAZURP2v",
                "yAjbLNTcqScHn", "XMjlZ", "hGfVWWSyCy4rimSDP", "RXi2yn1m6p3A32L0ZGdVmbIVqTAeNx8iSL",
                "eE3Awl2MCk35FUGp6LMqfFbqZrfGHT8", "qLYs8RR9kORJjLHlsEN9roMPO", "pcAnKbvIFmLvj4yN4u8S9KDzAbH", "XVSed",
                "IPpWNda1JyEPVYVMQF", "lRB5naIPvqFDy", "ysVyxqd9YjMPLWKwd6Aoed8", "72RtrI60xAGBqAxpieLv1d7D0iMr4",
                "jdPYYkGXWAJpFsTAe6hHk4DB", "ERh9aO9l", "oolHUkMbA3YItYylDTPIzVAh5q", "oyE4fB7hZMh0K",
                "d7yrtU0yBfMe3Y9S3krtFY", "MJrKJUJlrPiCMmhoy", "GUgMaBZQigpBQPP5p39ZdRooY2EvbcVTCXQ85AFS",
                "GUgMaBZQigpBQPP5p39ZdRooY2EvbcVTCXQ85AFS");
        SetBounds setBound = setBoundsHelper.initializeStringSetBound(smodelCreator, listOfDoubles, calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.STRING, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(3, result.getOptimizableValuesList()
            .size());
        assertEquals(40.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void simpleIntegerOptimizableRangeTest() throws IOException {
        RangeBounds rangeBound = new RangeBoundsHelper().initializeIntegerRangeBound(smodelCreator, calculator, 0, 20,
                1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(19.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void mediumIntegerOptimizableRangeTest() throws IOException {
        RangeBounds rangeBound = new RangeBoundsHelper().initializeIntegerRangeBound(smodelCreator, calculator, 0, 100,
                1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, atLeast(1)).reportStatus(any(Long.class), any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();
        assertEquals(99.0, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void largeIntegerOptimizableRangeTest() throws IOException {
        int lowerBound = 0;
        int upperBound = 100000;
        RangeBounds rangeBound = new RangeBoundsHelper().initializeIntegerRangeBound(smodelCreator, calculator,
                lowerBound, upperBound, 1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(99839.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(10)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void simpleIntegerOptimizableSetTestWithParetoEfficientElements() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 3, 7, 3, 8, 2, 9, 9),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        List<List<OptimizableValue<?>>> optimizableValuesList = result.getOptimizableValuesList();
        assertEquals(2, optimizableValuesList.size());
        assertEquals(9.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, optimizableValuesList.get(0)
            .get(0)
            .getOptimizable());
        assertEquals(9, optimizableValuesList.get(0)
            .get(0)
            .getValue());
        assertEquals(9, optimizableValuesList.get(1)
            .get(0)
            .getValue());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                0.00001);
    }

    @Test
    public void mediumIntegerOptimizableSetTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, 31, 84, 90, 40, 80, 28, 69, 74, 69, 29, 83, 31, 53, 35, 42, 80, 52, 85, 16), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(90.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void mediumIntegerOptimizableSetTestWithNegativeNumbers() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, -31, 84, -90, 40, -80, 28, 69, 74, 69, 29, 83, -31, 53, 35, -42, 80, 52, 85, -16),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(85.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void largeIntegerOptimizableSetTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(72, 57, 13, 25, 40, 8, 67, 49, 90, 58, 3, 27, 44, 91, 96, 39, 70, 20, 85, 78, 19, 34, 42, 95,
                        10, 87, 50, 4, 16, 61, 98, 88, 77, 23, 84, 63, 45, 33, 9, 56, 81, 18, 92, 31, 26, 5, 65, 60, 75,
                        80, 74, 36, 71, 97, 47, 1, 55, 43, 52, 2, 62, 21, 7, 48, 11, 32, 46, 73, 99, 79, 93, 64, 37, 29,
                        76, 53, 54, 17, 30, 28, 94, 22, 68, 14, 59, 24, 6, 35, 38, 89, 66, 41, 12, 15, 86, 82, 51, 83,
                        20, 0),
                calculator);
        double estimatedOptimumFitness = 99.0;
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(99.0, result.getFitness(), 0.00001);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(8)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void integerDoubleOptimizableRangeTest() throws IOException {
        IntLiteral lowerBound = smodelCreator.createIntLiteral(0);
        IntLiteral upperBound = smodelCreator.createIntLiteral(20);
        IntLiteral step = smodelCreator.createIntLiteral(1);
        when(calculator.calculateInteger(lowerBound)).thenReturn(lowerBound.getValue());
        when(calculator.calculateInteger(upperBound)).thenReturn(upperBound.getValue());
        when(calculator.calculateInteger(step)).thenReturn(step.getValue());
        RangeBounds intRangeBound = smodelCreator.createRangeBoundsClosedOpen(lowerBound, upperBound, step);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, intRangeBound);
        DoubleLiteral lowerBoundDouble = smodelCreator.createDoubleLiteral(0.0);
        DoubleLiteral upperBoundDouble = smodelCreator.createDoubleLiteral(20.0);
        DoubleLiteral stepDouble = smodelCreator.createDoubleLiteral(1.0);
        when(calculator.calculateDouble(lowerBoundDouble)).thenReturn(lowerBoundDouble.getValue());
        when(calculator.calculateDouble(upperBoundDouble)).thenReturn(upperBoundDouble.getValue());
        when(calculator.calculateDouble(stepDouble)).thenReturn(stepDouble.getValue());
        RangeBounds doubleRangeBound = smodelCreator.createRangeBoundsClosedOpen(lowerBoundDouble, upperBoundDouble,
                stepDouble);
        Optimizable doubleOptimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleRangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, doubleOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(38.0, result.getFitness(), DELTA);
        assertEquals(intOptimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        assertEquals(doubleOptimizable, result.getOptimizableValuesList()
            .get(0)
            .get(1)
            .getOptimizable());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(8)).reportStatus(any(Long.class), optimizableListCaptor.capture(),
                captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(37.0, 38.0, 38.0, 38.0, 38.0, 38.0, 38.0, 38.0);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                0.00001);
        List<Optimizable> expectedBestGenotypesFirstOptimizable = List.of(intOptimizable, intOptimizable,
                intOptimizable, intOptimizable, intOptimizable, intOptimizable, intOptimizable, intOptimizable);
        assertArrayEquals(expectedBestGenotypesFirstOptimizable.stream()
            .toArray(),
                optimizableListCaptor.getAllValues()
                    .stream()
                    .map((List<OptimizableValue<?>> l) -> l.get(0)
                        .getOptimizable())
                    .toArray());
        List<Optimizable> expectedBestGenotypesSecondOptimizable = List.of(doubleOptimizable, doubleOptimizable,
                doubleOptimizable, doubleOptimizable, doubleOptimizable, doubleOptimizable, doubleOptimizable,
                doubleOptimizable);
        assertArrayEquals(expectedBestGenotypesSecondOptimizable.stream()
            .toArray(),
                optimizableListCaptor.getAllValues()
                    .stream()
                    .map((List<OptimizableValue<?>> l) -> l.get(1)
                        .getOptimizable())
                    .toArray());
    }

    @Test
    public void integerDoubleOptimizableSetTest() throws IOException {
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds doubleSetBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0), calculator);
        Optimizable doubleOptimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleSetBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, doubleOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(18.0, result.getFitness(), 0.00001);
        assertEquals(intOptimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void integerBooleanOptimizableSetTest() throws IOException {
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds boolSetBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                calculator);
        Optimizable boolOptimizable = smodelCreator.createOptimizable("test", DataType.BOOL, boolSetBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, boolOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(59.0, result.getFitness(), 0.00001);
        assertEquals(intOptimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), any(Double.class));
    }

    @Test
    public void integerBooleanDoubleOptimizableSetTest() throws IOException {
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
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(68.0, result.getFitness(), 0.00001);
        assertEquals(intOptimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), any(List.class), captor.capture());
        Double[] expectedBestGenotypesFitnessEvolution = new Double[] { 68.0, 68.0, 68.0, 68.0, 68.0, 68.0, 68.0 };
        assertArrayEquals(expectedBestGenotypesFitnessEvolution, captor.getAllValues()
            .stream()
            .toArray());

    }

    @Test
    public void manySuboptimizablesOptimizableSetTest() throws IOException {
        Random r = new Random(42);
        Map<Optimizable, Object> optimizables = new LinkedHashMap();
        for (int i = 0; i < 15; i++) {
            double randDouble = r.nextDouble();
            if (randDouble < 0.25) {
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
            } else if (randDouble < 0.5) {
                // Double
                List<Double> numbers = new ArrayList<>();
                double max = 0;
                for (int upperBound = 0; upperBound < r.nextInt(1, 15); upperBound++) {
                    double nextDouble = r.nextDouble(100);
                    numbers.add(nextDouble);
                    if (nextDouble > max)
                        max = nextDouble;
                }
                SetBounds doubleSetBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, numbers, calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleSetBound), max);
            } else if (randDouble < 0.75) {
                // Double
                List<String> strings = new ArrayList<>();

                String max = "";
                for (int upperBound = 0; upperBound < r.nextInt(1, 15); upperBound++) {
                    String randomString = generateRandomString(r, r.nextInt(50));
                    strings.add(randomString);

                    if (randomString.length() > max.length())
                        max = randomString;
                }
                SetBounds stringSetBound = setBoundsHelper.initializeStringSetBound(smodelCreator, strings, calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.STRING, stringSetBound), max);
            } else {
                // Bool
                SetBounds boolSetBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                        calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.BOOL, boolSetBound), true);
            }

        }
        when(optimizableProvider.getOptimizables()).thenReturn(optimizables.keySet());
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double maximumFitness = 0;
        for (Object obj : optimizables.values()) {
            if (obj instanceof Boolean) {
                maximumFitness += 50;
            } else if (obj instanceof Double doubleVal) {
                maximumFitness += doubleVal;
            } else if (obj instanceof String stringVal) {
                maximumFitness += stringVal.length();
            } else {
                maximumFitness += (Integer) obj;
            }
        }
        LOGGER.info("Maximum Fitness: " + maximumFitness);
        assertEquals(1, result.getOptimizableValuesList()
            .size());
        assertEquals(949.1987, result.getFitness(), DELTA);
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(33)).reportStatus(any(Long.class), any(List.class), captor.capture());
        List<Double> capturedValues = captor.getAllValues();

        assertEquals(949.1987, capturedValues.get(capturedValues.size() - 1), DELTA);
    }

    @Test
    public void emptyChromosomeTest() throws IOException {
        Map<Optimizable, Object> optimizables = new LinkedHashMap<>();

        int maxInt = 100;
        List<Integer> ints = IntStream.rangeClosed(1, maxInt)
            .boxed()
            .toList();
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, ints, calculator);
        optimizables.put(smodelCreator.createOptimizable("test", DataType.INT, integerSetBound), maxInt);

        SetBounds stringSetBound = setBoundsHelper.initializeStringSetBound(smodelCreator,
                Collections.singletonList("single"), calculator);
        optimizables.put(smodelCreator.createOptimizable("test", DataType.STRING, stringSetBound), "single");
        when(optimizableProvider.getOptimizables()).thenReturn(optimizables.keySet());
        when(fitnessEvaluator.calcFitness(anyList())).thenAnswer(new Answer<Future<Optional<Double>>>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        double actualFitness = result.getFitness();
        assertEquals(106.0, actualFitness, DELTA);
    }

    private String generateRandomString(Random r, int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        return r.ints(leftLimit, rightLimit + 1)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

    }
}
