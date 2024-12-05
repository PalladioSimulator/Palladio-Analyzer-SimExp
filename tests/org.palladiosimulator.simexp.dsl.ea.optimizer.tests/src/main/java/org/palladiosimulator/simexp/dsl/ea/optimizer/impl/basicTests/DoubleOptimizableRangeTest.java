package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.basicTests;

import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.DecoderEncodingPair;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;
import utility.RangeBoundsHelper;

public class DoubleOptimizableRangeTest {

    private static final double DOUBLE_EPSILON = 1e-15;

    private static final double EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS = 0.9;

    @Mock
    private IEAConfig eaConfig;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    @Mock
    private ISmodelConfig smodelConfig;

    private SmodelCreator smodelCreator;

    @Mock
    private IExpressionCalculator calculator;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private IEAOptimizer optimizer;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(smodelConfig.getEpsilon()).thenReturn(DOUBLE_EPSILON);
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();

        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        optimizer = eaOptimizer.create(eaConfig);
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

                return getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(19.0));
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

                return getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), gt(upperBound * EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS));
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

                return getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), gt(upperBound * EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS));
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

                return getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(9.65));
    }

    private Future<Double> getFitnessFunctionAsFuture(InvocationOnMock invocation) {
        return executor.submit(() -> {
            List<IEAFitnessEvaluator.OptimizableValue> optimizableValues = invocation.getArgument(0);

            double value = 0;

            for (IEAFitnessEvaluator.OptimizableValue singleOptimizableValue : optimizableValues) {
                DecoderEncodingPair chromoPair = (DecoderEncodingPair) singleOptimizableValue.getValue();
                DataType optimizableDataType = singleOptimizableValue.getOptimizable()
                    .getDataType();

                Object apply = chromoPair.first()
                    .apply(chromoPair.second());

                if (apply instanceof ArrayISeq arraySeq) {
                    if (arraySeq.size() == 1) {
                        for (Object element : arraySeq.array) {
                            if (optimizableDataType == DataType.DOUBLE) {
                                value += (Double) element;
                            }
                        }
                    }
                } else if (apply instanceof EmptyISeq emptySeq) {
                    // do nothing

                } else {
                    throw new RuntimeException("Unknown chromosome type specified: " + chromoPair.second()
                        .getClass());
                }
            }

            return value;
        });
    }

};