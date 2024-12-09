package combinedChromosomeTests;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
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
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;
import utility.SetBoundsHelper;

//review-finding-2024-12-09: 
//  - class under test is 'EAOptimizer' thus rename test class to EAOptimizerTest; 
//  - package naming convention violated; CUT should be placed in a package of same name as in 'impl' -> org.palladiosimulator.simexp.dsl.ea.optimizer.impl
//  - chromosome tests should be realized as several test methods within this test class                    
public class CombinedSetChromosomeTest {

    private static final double DOUBLE_EPSILON = 1e-15;

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

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

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(38));
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

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(38));
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

                return getFitnessFunctionAsFuture(invocation);
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

                return getFitnessFunctionAsFuture(invocation);
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

        verify(statusReceiver).reportStatus(any(List.class), eq(maximumFitness));
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
                        if (optimizableDataType == DataType.DOUBLE) {
                            for (Object element : arraySeq.array) {
                                value += (Double) element;
                            }
                        } else if (optimizableDataType == DataType.INT) {
                            for (Object element : arraySeq.array) {
                                value += (Integer) element;
                            }
                        }
                    }
                } else if (apply instanceof EmptyISeq emptySeq) {
                    // do nothing
                } else if (optimizableDataType == DataType.BOOL) {
                    if ((Boolean) apply) {
                        value += 50;
                    }
                } else {
                    throw new RuntimeException("Unknown chromosome type specified: " + chromoPair.second()
                        .getClass());
                }
            }

            return value;
        });
    }

}
