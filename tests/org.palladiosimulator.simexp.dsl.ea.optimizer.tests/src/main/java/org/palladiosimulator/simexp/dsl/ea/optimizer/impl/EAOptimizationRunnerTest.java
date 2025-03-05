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
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.ConfigHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.IntRange;
import io.jenetics.util.RandomRegistry;

public class EAOptimizationRunnerTest {

    private static final double DELTA = 0.0001;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private MOEAFitnessFunction fitnessFunction;

    @Mock
    private IExpressionCalculator expressionCalculator;

    @Captor
    private ArgumentCaptor<List<OptimizableValue<?>>> optimizableListCaptor;

    @Captor
    ArgumentCaptor<Double> fitnessCaptor;

    private EAOptimizationRunner objectUnderTest;

    private OptimizableIntegerChromoNormalizer optimizableNormalizer;

    private SmodelCreator smodelCreator;

    private Optimizable optimizable;

    private Function<? super Random, EAResult> optFunction;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        objectUnderTest = new EAOptimizationRunner();
        when(fitnessFunction.apply(any())).then(new Answer<Vec<double[]>>() {
            @Override
            public Vec<double[]> answer(InvocationOnMock invocation) throws Throwable {
                SmodelIntegerChromosome as = (SmodelIntegerChromosome) invocation.getArgument(0, Genotype.class)
                    .chromosome();
                return Vec.of(new double[] { as.intValue() });
            }

        });
        IEAConfig config = new ConfigHelper(10, 0.2, 0.2, 7, 50);

        optimizableNormalizer = new OptimizableIntegerChromoNormalizer(expressionCalculator);

        IntRange intRange = IntRange.of(0, 99);
        RangeBounds rangeBound = new RangeBoundsHelper().initializeIntegerRangeBound(smodelCreator,
                expressionCalculator, 0, 100, 1);
        optimizable = smodelCreator.createOptimizable("test", DataType.INT, rangeBound);
        Genotype<IntegerGene> genotype = Genotype.of(SmodelIntegerChromosome.of(intRange, optimizable));
        Engine<IntegerGene, Vec<double[]>> engine = new EAOptimizationEngineBuilder(config).buildEngine(fitnessFunction,
                genotype, Runnable::run);
        optFunction = r -> {
            return objectUnderTest.runOptimization(statusReceiver, optimizableNormalizer, fitnessFunction, engine,
                    config);
        };
    }

    @Test
    public void testOptimizationEAResult() {

        EAResult runOptimization = RandomRegistry.with(new Random(42), optFunction);

        assertEquals(99.0, runOptimization.getFitness(), DELTA);
        assertEquals(99, (int) runOptimization.getOptimizableValuesList()
            .get(0)
            .get(0)
            .getValue());
        assertEquals(optimizable, runOptimization.getOptimizableValuesList()
            .get(0)
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
