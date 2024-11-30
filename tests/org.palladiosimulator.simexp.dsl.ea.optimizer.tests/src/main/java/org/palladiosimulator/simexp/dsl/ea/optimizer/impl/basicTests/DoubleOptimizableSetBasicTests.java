package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.basicTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
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
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;

public class DoubleOptimizableSetBasicTests {

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
        SetBounds setBound = initializeDoubleSetBound(List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0), calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
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
        List<Double> listOfDoubles = List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999, 64.0,
                64.43, 99.99, 23.4, 45.4, 88.56, 93.22, 22.0, 19.34, 85.5);
        SetBounds setBound = initializeDoubleSetBound(listOfDoubles, calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.999));
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
        SetBounds setBound = initializeDoubleSetBound(listOfDoubles, calculator);

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, setBound);
        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class), eq(99.999));
    }

    private SetBounds initializeDoubleSetBound(List<Double> elementsInSet, IExpressionCalculator calculator) {
        List<DoubleLiteral> doubleLiterals = new ArrayList();
        for (Double element : elementsInSet) {
            DoubleLiteral elementLiteral = smodelCreator.createDoubleLiteral(element);
            doubleLiterals.add(elementLiteral);
            when(calculator.calculateDouble(elementLiteral)).thenReturn(element);
        }
        DoubleLiteral[] doubleLiteralsAsArray = doubleLiterals.toArray(new DoubleLiteral[doubleLiterals.size()]);
        return smodelCreator.createSetBounds(doubleLiteralsAsArray);
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
                            if (element instanceof Double doubleValue) {
                                value += doubleValue;
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
