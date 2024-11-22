package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class TwoDoubleOptimizableRangeTest {

    @Mock
    private IEAConfig eaConfig;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    private SmodelCreator smodelCreator;

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
//        initMocks(this);
        smodelCreator = new SmodelCreator();

    }

    @Test
    public void optimizeTest() {
        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        IEAOptimizer optimizer = eaOptimizer.create(eaConfig);

        RangeBounds rangeBound = smodelCreator.createRangeBounds(smodelCreator.createDoubleLiteral(0.0),
                smodelCreator.createDoubleLiteral(10.0), smodelCreator.createDoubleLiteral(1.0));

        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, rangeBound);

        when(optimizableProvider.getOptimizables()).thenReturn(List.of(optimizable));

        optimizer.optimize(null, fitnessEvaluator, statusReceiver);

    }

};