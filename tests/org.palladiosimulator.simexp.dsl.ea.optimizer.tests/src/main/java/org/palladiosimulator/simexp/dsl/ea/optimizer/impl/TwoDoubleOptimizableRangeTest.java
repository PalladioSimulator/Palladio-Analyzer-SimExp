package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class TwoDoubleOptimizableRangeTest {

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

    private IFieldValueProvider fieldValueProvider;

    private SmodelCreator smodelCreator;

    private ExpressionCalculator calculator;

    private ParseHelper<Smodel> parserHelper;

    private ValidationTestHelper validationTestHelper;

//    @Mock
//    private ISmodelConfig smodelConfig;
//    
//    @Mock
//    private IFieldValueProvider fieldValueProvider;
//    
//    public void setUp() {
//        new ExpressionCalculator();
//    }

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(smodelConfig.getEpsilon()).thenReturn(DOUBLE_EPSILON);
        calculator = new ExpressionCalculator(smodelConfig, fieldValueProvider);
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Smodel>>() {
        }));
        validationTestHelper = injector.getInstance(ValidationTestHelper.class);
    }

    @Test
    public void optimizeTest() {
        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        IEAOptimizer optimizer = eaOptimizer.create(eaConfig);

        RangeBounds rangeBound = smodelCreator.createRangeBounds(smodelCreator.createDoubleLiteral(0.0),
                smodelCreator.createDoubleLiteral(10.0), smodelCreator.createDoubleLiteral(1.0));

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);

        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

    }

};