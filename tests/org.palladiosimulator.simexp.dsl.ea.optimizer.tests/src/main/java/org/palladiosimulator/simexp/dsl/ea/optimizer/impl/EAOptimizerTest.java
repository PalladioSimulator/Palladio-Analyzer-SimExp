package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
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

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.util.DoubleComparator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
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
    private Function<Random, EAResult> optFunction;
    private SetBoundsHelper setBoundsHelper;
    private RangeBoundsHelper rangeBoundsHelper;
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
        rangeBoundsHelper = new RangeBoundsHelper();

        threadLocalRandom = ThreadLocal.withInitial(() -> new Random(42));
        optFunction = r -> {
            return optimizer.internalOptimize(optimizableProvider, fitnessEvaluator, statusReceiver, Runnable::run);
        };
        when(eaConfig.populationSize()).thenReturn(SimulationConstants.DEFAULT_POPULATION_SIZE);
        when(eaConfig.mutationRate()).thenReturn(Optional.of(SimulationConstants.DEFAULT_MUTATION_RATE));
        when(eaConfig.crossoverRate()).thenReturn(Optional.of(SimulationConstants.DEFAULT_CROSSOVER_RATE));
        when(eaConfig.steadyFitness()).thenReturn(Optional.of(7));
        when(eaConfig.maxGenerations()).thenReturn(Optional.of(100));

        optimizer = new EAOptimizer(eaConfig);
    }

    @Test
    public void booleanTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.BOOL, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 50.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of(true));
    }

    @Test
    public void doubleTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, List.of(8.0, 9.0), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 9.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
            .withComparatorForType(new DoubleComparator(DELTA), Double.class)
            .build();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .usingRecursiveFieldByFieldElementComparator(configuration)
            .containsExactlyInAnyOrder(List.of(9.0));
    }

    @Test
    public void doubleMultipleParetoOptimalElementsTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, List.of(9.00001, 9.0), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 9.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
            .withComparatorForType(new DoubleComparator(DELTA), Double.class)
            .build();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .usingRecursiveFieldByFieldElementComparator(configuration)
            .containsExactlyInAnyOrder(List.of(9.0), List.of(9.0));
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
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of("123456"));
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
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of("abcdef"), List.of("youuuu"));
    }

    @Test
    public void integerSetTest() throws IOException {
        SetBounds bound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 10, 19), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, bound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 19.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of(19));
    }

    @Test
    public void integerRangeTest() throws IOException {
        RangeBounds bound = rangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 10, 1);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, bound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 9.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of(9));
    }

    @Test
    public void integerMultipleParetoOptimalElementsTest() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 3, 7, 3, 8, 9, 9),
                calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 9.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of(9), List.of(9));
    }

    @Test
    public void integerWithNegativeNumbers() throws IOException {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(4, 85, -31), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 85.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of(85));
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

        double expectedFitness = 68.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
            .withComparatorForType(new DoubleComparator(DELTA), Double.class)
            .build();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .usingRecursiveFieldByFieldElementComparator(configuration)
            .containsExactlyInAnyOrder(List.of(9, true, 9.0));
    }

    @Test
    public void emptyChromosomeTest() throws IOException {
        Map<Optimizable, Object> optimizables = new LinkedHashMap<>();
        int maxInt = 10;
        List<Integer> ints = IntStream.rangeClosed(1, maxInt)
            .boxed()
            .toList();
        SetBounds integerSetBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, ints, calculator);
        optimizables.put(smodelCreator.createOptimizable("int", DataType.INT, integerSetBound), maxInt);
        SetBounds stringSetBound = setBoundsHelper.initializeStringSetBound(smodelCreator,
                Collections.singletonList("single"), calculator);
        optimizables.put(smodelCreator.createOptimizable("str", DataType.STRING, stringSetBound), "single");
        when(optimizableProvider.getOptimizables()).thenReturn(optimizables.keySet());

        EAResult result = RandomRegistry.with(threadLocalRandom, optFunction);

        double expectedFitness = 16.0;
        assertEquals(expectedFitness, result.getFitness(), DELTA);
        List<List<OptimizableValue<?>>> actualOptimizableValues = result.getOptimizableValuesList();
        assertThat(actualOptimizableValues).<Object> extracting(l -> l.stream()
            .map(v -> v.getValue())
            .toList())
            .containsExactlyInAnyOrder(List.of(10, "single"));
    }
}
