package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.util.RandomRegistry;

public class EAOptimizationRunnerTest {

    private static final double DELTA = 0.0001;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private FitnessFunction fitnessFunction;

    @Mock
    private IExpressionCalculator expressionCalculator;

    @Captor
    private ArgumentCaptor<List<OptimizableValue<?>>> optimizableListCaptor;

    @Captor
    ArgumentCaptor<Double> fitnessCaptor;

    private EAOptimizationRunner objectUnderTest;

    private OptimizableNormalizer optimizableNormalizer;

    private SmodelCreator smodelCreator;

    private Optimizable optimizable;

    private Function<? super Random, EAResult> optFunction;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        objectUnderTest = new EAOptimizationRunner();
        when(fitnessFunction.apply(any())).then(new Answer<Double>() {
            @Override
            public Double answer(InvocationOnMock invocation) throws Throwable {
                SmodelBitChromosome as = (SmodelBitChromosome) invocation.getArgument(0, Genotype.class)
                    .chromosome();
                return (double) as.intValue();
            }

        });

        optimizableNormalizer = new OptimizableNormalizer(expressionCalculator);

        SmodelBitset smodelBitset = new SmodelBitset(7);
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, expressionCalculator, 0,
                100, 1);
        optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        Genotype<BitGene> genotype = Genotype.of(SmodelBitChromosome.of(smodelBitset, optimizable, 100));
        Engine<BitGene, Double> engine = new OptimizationEngineBuilder().buildEngine(fitnessFunction, genotype, 10,
                Runnable::run, 5, 5, 0.2, 0.2);
        optFunction = r -> {
            return objectUnderTest.runOptimization(statusReceiver, optimizableNormalizer, engine);
        };
    }

    @Test
    public void testOptimizationEAResult() {

        EAResult runOptimization = RandomRegistry.with(new Random(42), optFunction);

        assertEquals(95.0, runOptimization.getFitness(), DELTA);
        assertEquals(95, (int) runOptimization.getOptimizableValues()
            .get(0)
            .getValue());
        assertEquals(optimizable, runOptimization.getOptimizableValues()
            .get(0)
            .getOptimizable());
    }

    public void testOptimizationEvolutionStatusReceiver() {

        EAResult runOptimization = RandomRegistry.with(new Random(42), optFunction);

        verify(statusReceiver).reportStatus(any(Long.class), optimizableListCaptor.capture(), fitnessCaptor.capture());
        optimizableListCaptor.getAllValues()
            .get(0);

        // TODO nbruening: Hier noch den Test vervollständigen

//        optimizableListCaptor.getAllValues().get()

    }

}