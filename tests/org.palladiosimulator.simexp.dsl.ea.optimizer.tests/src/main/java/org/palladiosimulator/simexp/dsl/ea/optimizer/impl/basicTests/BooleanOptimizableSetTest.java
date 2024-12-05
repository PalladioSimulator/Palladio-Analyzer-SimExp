package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.basicTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.DecoderEncodingPair;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import utility.SetBoundsHelper;

public class BooleanOptimizableSetTest {

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

    private IEAOptimizer optimizer;

    @Captor
    private ArgumentCaptor<List<IEAFitnessEvaluator.OptimizableValue<?>>> optimizableListCaptor;

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

                return getFitnessFunctionAsFuture(invocation);
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

    private Future<Double> getFitnessFunctionAsFuture(InvocationOnMock invocation) {
        return Executors.newSingleThreadExecutor()
            .submit(() -> {
                List<IEAFitnessEvaluator.OptimizableValue> optimizableValues = invocation.getArgument(0);

                double value = 0;

                for (IEAFitnessEvaluator.OptimizableValue singleOptimizableValue : optimizableValues) {
                    DecoderEncodingPair chromoPair = (DecoderEncodingPair) singleOptimizableValue.getValue();
                    DataType optimizableDataType = singleOptimizableValue.getOptimizable()
                        .getDataType();

                    Object apply = chromoPair.first()
                        .apply(chromoPair.second());

                    if (optimizableDataType == DataType.BOOL) {
                        if ((Boolean) apply) {
                            value += 50;
                        }
                    } else {
                        throw new RuntimeException("Expected Bool, but got: " + optimizableDataType);
                    }
                }

                return value;
            });
    }

}
