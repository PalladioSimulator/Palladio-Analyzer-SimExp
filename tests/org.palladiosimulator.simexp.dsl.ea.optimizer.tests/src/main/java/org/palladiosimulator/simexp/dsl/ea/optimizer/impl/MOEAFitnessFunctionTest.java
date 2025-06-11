package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.BinaryBitInterpreter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;

public class MOEAFitnessFunctionTest {
    private static final double BIG_TOO_LONG_FLOATING_POINT_NUMBER = 50.12345678901234567890123456789;

    private static final double DELTA = 0.0001;

    private static final double TOO_LONG_FLOATING_POINT_NUMBER = 0.123456789;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;
    @Mock
    private OptimizableNormalizer normalizer;
    @Mock
    private Optimizable optimizable;
    @Mock
    private Future<Optional<Double>> fitnessFuture;
    @Mock
    private ExecutionException executionException;
    @Mock
    private OptimizableValue<Double> optimizableValue;
    @Captor
    private ArgumentCaptor<List<OptimizableValue<?>>> captor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApply() throws IOException, InterruptedException, ExecutionException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        OptimizableValue<Double> optimizableValue = mock(OptimizableValue.class);
        when(normalizer.toOptimizableValues(genotype)).thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenReturn(Optional.of(50.0));
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer, 0.0);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(50.0, actualFitness, DELTA);
        ArgumentCaptor<List<OptimizableValue<?>>> captor = ArgumentCaptor.forClass(List.class);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testHandleInterruptedException() throws InterruptedException, ExecutionException, IOException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(new InterruptedException("This is a test"));
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer,
                TOO_LONG_FLOATING_POINT_NUMBER);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, actualFitness, DELTA);
    }

    @Test
    public void testHandleExecutionException() throws InterruptedException, ExecutionException, IOException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer, 0.0);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, actualFitness, DELTA);
    }

    @Test
    public void testInvalidChromosome() throws InterruptedException, ExecutionException, IOException {
        SmodelBitset smodelBitset = new SmodelBitset(4);
        smodelBitset.set(3);
        smodelBitset.set(2);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(smodelBitset, optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer, 0.0);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, actualFitness, DELTA);
    }

    @Test
    public void testRoundingApply() throws IOException, InterruptedException, ExecutionException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(normalizer.toOptimizableValues(genotype)).thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenReturn(Optional.of(50.12345678901234567890123456789));
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer, 0.0);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(50.1235, actualFitness, DELTA);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testRoundingApplyMimimumDelta() throws IOException, InterruptedException, ExecutionException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(normalizer.toOptimizableValues(genotype)).thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenReturn(Optional.of(BIG_TOO_LONG_FLOATING_POINT_NUMBER));
        double smallEpsilon = 0.0000000000001;
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(smallEpsilon, fitnessEvaluator, normalizer, 0.0);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(50.12345678901234, actualFitness, smallEpsilon);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testRoundingInvalidChromosome() throws InterruptedException, ExecutionException, IOException {
        SmodelBitset smodelBitset = new SmodelBitset(4);
        smodelBitset.set(3);
        smodelBitset.set(2);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(smodelBitset, optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer,
                TOO_LONG_FLOATING_POINT_NUMBER);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, actualFitness, DELTA);
    }
}
