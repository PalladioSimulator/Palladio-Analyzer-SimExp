package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.basicTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.Pair;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;
import utility.SetBoundsHelper;

public class IntegerOptimizableSetBasicTest {

    @Mock
    private IEAConfig eaConfig;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    private SmodelCreator smodelCreator;

    @Mock
    private IExpressionCalculator calculator;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private IEAOptimizer optimizer;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();

        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        optimizer = eaOptimizer.create(eaConfig);
    }

    @Test
    public void simpleDoubleOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 3, 7, 3, 8, 2, 9),
                calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(9.0));
    }

    @Test
    public void mediumDoubleOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, 31, 84, 90, 40, 80, 28, 69, 74, 69, 29, 83, 31, 53, 35, 42, 80, 52, 85, 16), calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(90.0));
    }

    @Test
    public void mediumDoubleOptimizableSetTestWithNegativeNumbers() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(4, -31, 84, -90, 40, -80, 28, 69, 74, 69, 29, 83, -31, 53, 35, -42, 80, 52, 85, -16),
                calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(85.0));
    }

    @Test
    public void largeDoubleOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
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

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.0));
    }

    private Future<Double> getFitnessFunctionAsFuture(InvocationOnMock invocation) {
        return executor.submit(() -> {
            List<IEAFitnessEvaluator.OptimizableValue> optimizableValues = invocation.getArgument(0);

            double value = 0;

            for (IEAFitnessEvaluator.OptimizableValue singleOptimizableValue : optimizableValues) {
                Pair chromoPair = (Pair) singleOptimizableValue.getValue();

                Object apply = ((Function) chromoPair.first()).apply(chromoPair.second());

                if (apply instanceof ArrayISeq arraySeq) {
                    if (arraySeq.size() == 1) {
                        for (Object element : arraySeq.array) {
                            if (element instanceof Integer integerValue) {
                                value += integerValue;
                            }
                        }
                    }
                } else if (apply instanceof EmptyISeq emptySeq) {
                    System.out.println("empty seq");
                    // do nothing
                } else {
                    throw new RuntimeException("Unknown chromosome type specified: " + chromoPair.second()
                        .getClass());
                }
            }

            return value;
        });
    }
}
