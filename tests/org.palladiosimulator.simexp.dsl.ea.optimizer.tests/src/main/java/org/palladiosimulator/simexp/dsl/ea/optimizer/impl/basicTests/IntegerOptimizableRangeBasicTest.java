package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.basicTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
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
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.FitnessHelper;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import utility.RangeBoundsHelper;

public class IntegerOptimizableRangeBasicTest {

    private static final double DOUBLE_EPSILON = 1e-15;

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

    private IEAOptimizer optimizer;

    private FitnessHelper fitnessHelper;

    @Before
    public void setUp() {
        initMocks(this);

        smodelCreator = new SmodelCreator();
        fitnessHelper = new FitnessHelper();
        when(smodelConfig.getEpsilon()).thenReturn(DOUBLE_EPSILON);
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();

        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        optimizer = eaOptimizer.create(eaConfig);
    }

    @Test
    public void simpleDoubleOptimizableRangeTest() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 20, 1);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {

            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(19.0));
    }

    @Test
    public void mediumDoubleOptimizableRangeTest() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 100, 1);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {

            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.0));
    }

    @Test
    public void largeDoubleOptimizableRangeTest() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 1000, 1);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {

            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(999.0));
    }

}
