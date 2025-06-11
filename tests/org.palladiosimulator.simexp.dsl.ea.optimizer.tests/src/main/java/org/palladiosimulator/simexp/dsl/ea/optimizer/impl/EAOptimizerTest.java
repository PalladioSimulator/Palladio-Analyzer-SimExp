package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.IntStream;

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
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.ConfigHelper;
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
    private Function<Random, EAResult> optFunction;
    private SetBoundsHelper setBoundsHelper;
    private Answer<Future<Optional<Double>>> fitnessAnswer;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);
        when(calculator.getEpsilon()).thenReturn(DELTA);
        fitnessAnswer = new Answer<>() {
            @Override
            public Future<Optional<Double>> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        };
        when(fitnessEvaluator.calcFitness(anyList())).thenAnswer(fitnessAnswer);

        setBoundsHelper = new SetBoundsHelper();

        threadLocalRandom = ThreadLocal.withInitial(() -> new Random(42));
        optFunction = r -> {
            return optimizer.internalOptimize(optimizableProvider, fitnessEvaluator, statusReceiver, Runnable::run);
        };
        eaConfig = new ConfigHelper(80, 0.01, 0.8, 7, 100);

        optimizer = new EAOptimizer(eaConfig);
    }

    @Test
    public void booleanTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.BOOL, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

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
    public void doubleTest() throws IOException {
        double expectedFitness = 9.0;
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, expectedFitness, expectedFitness, expectedFitness), calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(3, result.getOptimizableValuesList()
            .size());
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> optimizableValuesList = result.getOptimizableValuesList();
        assertEquals(optimizable, optimizableValuesList.get(0)
            .get(0)
            .getOptimizable());
        assertEquals(expectedFitness, optimizableValuesList.get(0)
            .get(0)
            .getValue());
        assertEquals(expectedFitness, optimizableValuesList.get(1)
            .get(0)
            .getValue());
        assertEquals(expectedFitness, optimizableValuesList.get(2)
            .get(0)
            .getValue());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), anyList(), captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(expectedFitness, expectedFitness, expectedFitness,
                expectedFitness, expectedFitness, expectedFitness, expectedFitness);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                DELTA);
    }

    @Test
    public void stringTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeStringSetBound(smodelCreator, List.of("Hello", "123456"),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.STRING, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 6.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).extracting(l -> l.get(0)
            .getValue()
            .toString())
            .containsExactlyInAnyOrder("123456");
    }

    @Test
    public void stringMultipleParetoOptimalElementsTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeStringSetBound(smodelCreator,
                List.of("Hello", "youuuu", "abcdef"), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.STRING, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 6.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).extracting(l -> l.get(0)
            .getValue()
            .toString())
            .containsExactlyInAnyOrder("abcdef", "youuuu");
    }

    @Test
    public void integerTest() throws IOException {
        RangeBounds rangeBound = new RangeBoundsHelper().initializeIntegerRangeBound(smodelCreator, calculator, 0, 20,
                1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        double expectedFitness = 19.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), anyList(), any(Double.class));
    }

    @Test
    public void integerMultipleParetoOptimalElementsTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 3, 7, 3, 8, 2, 9, 9),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        List<List<OptimizableValue<?>>> optimizableValuesList = result.getOptimizableValuesList();
        assertEquals(2, optimizableValuesList.size());
        double expectedFitness = 9.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
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
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), anyList(), captor.capture());
        List<Double> fitnessEvolutionValues = captor.getAllValues();
        List<Double> expectedFitnessEvolution = List.of(expectedFitness, expectedFitness, expectedFitness,
                expectedFitness, expectedFitness, expectedFitness, expectedFitness);
        assertArrayEquals(expectedFitnessEvolution.stream()
            .mapToDouble(Double::doubleValue)
            .toArray(),
                fitnessEvolutionValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray(),
                DELTA);
    }

    @Test
    public void integerWithNegativeNumbers() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, -31, 84, -90, 40, -80, 28, 69, 74, 69, 29, 83, -31, 53, 35, -42, 80, 52, 85, -16),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        double expectedFitness = 85.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        assertEquals(optimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), anyList(), any(Double.class));
    }

    @Test
    public void integerBooleanDoubleTest() throws IOException {
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

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        double expectedFitness = 68.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        assertEquals(intOptimizable, result.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getOptimizable());
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(statusReceiver, times(7)).reportStatus(any(Long.class), anyList(), captor.capture());
        Double[] expectedBestGenotypesFitnessEvolution = new Double[] { expectedFitness, expectedFitness,
                expectedFitness, expectedFitness, expectedFitness, expectedFitness, expectedFitness };
        assertArrayEquals(expectedBestGenotypesFitnessEvolution, captor.getAllValues()
            .stream()
            .toArray());

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

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        assertEquals(1, result.getOptimizableValuesList()
            .size());
        double actualFitness = result.getFitness();
        assertEquals(106.0, actualFitness, DELTA);
    }
}
