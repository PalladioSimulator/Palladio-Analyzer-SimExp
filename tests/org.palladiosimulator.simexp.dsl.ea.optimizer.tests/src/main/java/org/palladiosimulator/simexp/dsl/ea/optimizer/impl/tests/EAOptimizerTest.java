package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.FitnessHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

public class EAOptimizerTest {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    private static final double EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS = 0.8;

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
    private ArgumentCaptor<List<IEAFitnessEvaluator.OptimizableValue<?>>> optimizableListCaptor;

    private SmodelCreator smodelCreator;

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
    public void simpleBooleanOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.BOOL, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(optimizableListCaptor.capture(), eq(50.0));
        List<List<OptimizableValue<?>>> allValues = optimizableListCaptor.getAllValues();
        assertEquals(1, allValues.size());
        assertEquals(1, allValues.get(0)
            .size());
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
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
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
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
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
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }

        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(9.65));
    }

    @Test
    public void simpleDoubleOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator,
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(9.0));
    }

    @Test
    public void mediumDoubleOptimizableSetTest() {
        List<Double> listOfDoubles = List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999, 64.0,
                64.43, 99.99, 23.4, 45.4, 88.56, 93.22, 22.0, 19.34, 85.5);
        SetBounds setBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator, listOfDoubles, calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.999));
    }

    // TODO This test should run green
    @Ignore("EA doesn't provide the necessary accuracy yet")
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
        SetBounds setBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator, listOfDoubles, calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.999));
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(19.0));
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.0));
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), gt(upperBound * EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS));
    }

    @Test
    public void simpleIntegerOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 3, 7, 3, 8, 2, 9),
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(9.0));
    }

    @Test
    public void mediumIntegerOptimizableSetTest() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(90.0));
    }

    @Test
    public void mediumIntegerOptimizableSetTestWithNegativeNumbers() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(85.0));
    }

    // TODO This test should run green
    @Ignore("EA doesn't provide the necessary accuracy yet")
    @Test
    public void largeIntegerOptimizableSetTest() {
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
                FitnessHelper fitnessHelper = new FitnessHelper();
                return fitnessHelper.getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.0));
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class),
                gt((upperBoundInteger + upperBoundDouble) * EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS));
    }

    @Test
    public void integerDoubleOptimizableSetTest() {
        SetBounds integerSetBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds doubleSetBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator,
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(18.0));
    }

    @Test
    public void integerBooleanOptimizableSetTest() {
        SetBounds integerSetBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds boolSetBound = SetBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(59.0));
    }

    @Test
    public void integerBooleanDoubleOptimizableSetTest() {
        SetBounds integerSetBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator,
                List.of(1, 3, 7, 3, 8, 2, 9), calculator);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, integerSetBound);
        SetBounds boolSetBound = SetBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                calculator);
        Optimizable boolOptimizable = smodelCreator.createOptimizable("test", DataType.BOOL, boolSetBound);
        SetBounds doubleSetBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator,
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(68.0));
    }

    @Test
    public void manySuboptimizablesOptimizableSetTest() {
        Random r = new Random();
        Map<Optimizable, Object> optimizables = new HashMap();
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
                SetBounds integerSetBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator, numbers,
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
                SetBounds doubleSetBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator, numbers, calculator);
                optimizables.put(smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleSetBound), max);
            } else {
                // Bool
                SetBounds boolSetBound = SetBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
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

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

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

        verify(statusReceiver).reportStatus(any(List.class),
                gt(maximumFitness * EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS));
    }
}
