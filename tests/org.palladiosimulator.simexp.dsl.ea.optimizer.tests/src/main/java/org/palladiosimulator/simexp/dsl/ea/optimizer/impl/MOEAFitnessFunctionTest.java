package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.BinaryBitInterpreter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class MOEAFitnessFunctionTest {
    private static final double BIG_TOO_LONG_FLOATING_POINT_NUMBER = 50.12345678901234567890123456789;

    private static final double DELTA = 0.00001;

    private static final double TOO_LONG_FLOATING_POINT_NUMBER = 0.123456789;

    @Mock
    IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    OptimizableIntegerChromoNormalizer normalizer;

    @Mock
    Optimizable optimizable;

    @Mock
    Future<Optional<Double>> fitnessFuture;

    @Mock
    ExecutionException executionException;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApply() {
        // Arrange
        SmodelIntegerChromosome chromosome = SmodelIntegerChromosome.of(IntRange.of(0, 3), optimizable);
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        OptimizableValue<Double> optimizableValue = mock(OptimizableValue.class);
        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s.contains(chromosome))))
            .thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        try {
            when(fitnessFuture.get()).thenReturn(Optional.of(50.0));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

        // Act
        Double fitness = fitnessFunction.apply(genotype);

        // Assert
        assertEquals(50.0, fitness, DELTA);
        ArgumentCaptor<List<OptimizableValue<?>>> captor = ArgumentCaptor.forClass(List.class);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testHandleInterruptedException() throws InterruptedException, ExecutionException {
        SmodelIntegerChromosome chromosome = SmodelIntegerChromosome.of(IntRange.of(0, 3), optimizable);
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(new InterruptedException("This is a test"));
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer,
                TOO_LONG_FLOATING_POINT_NUMBER);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, fitness, 0.00001);
    }

    @Test
    public void testHandleExecutionException() throws InterruptedException, ExecutionException {
        SmodelIntegerChromosome chromosome = SmodelIntegerChromosome.of(IntRange.of(0, 3), optimizable);
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

        Double fitness = fitnessFunction.apply(genotype);
        assertEquals(0.0, fitness, DELTA);
    }


    @Test
    public void testInvalidChromosome() throws InterruptedException, ExecutionException {
        SmodelIntegerChromosome chromosome = SmodelIntegerChromosome.of(IntRange.of(0, 3), optimizable);
        ISeq<IntegerGene> genes = ISeq.of(IntegerGene.of(4, IntRange.of(0, 3)));
        IntegerChromosome chromoWithFixedValue = chromosome.newInstance(genes);

        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, fitness, DELTA);
    }

    @Test
    public void testRoundingApply() {
        // Arrange
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        OptimizableValue<Double> optimizableValue = mock(OptimizableValue.class);
        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s.contains(chromosome))))
            .thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        try {
            when(fitnessFuture.get()).thenReturn(Optional.of(50.12345678901234567890123456789));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

        // Act
        Double fitness = fitnessFunction.apply(genotype);

        // Assert
        assertEquals(50.1235, fitness, DELTA);
        ArgumentCaptor<List<OptimizableValue<?>>> captor = ArgumentCaptor.forClass(List.class);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testRoundingApplyMimimumDelta() {
        // Arrange
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        OptimizableValue<Double> optimizableValue = mock(OptimizableValue.class);
        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s.contains(chromosome))))
            .thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        try {
            when(fitnessFuture.get()).thenReturn(Optional.of(BIG_TOO_LONG_FLOATING_POINT_NUMBER));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
        double smallEpsilon = 0.0000000000001;
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(smallEpsilon, fitnessEvaluator, normalizer);

        // Act
        Double fitness = fitnessFunction.apply(genotype);

        // Assert
        assertEquals(50.12345678901234, fitness, smallEpsilon);
        ArgumentCaptor<List<OptimizableValue<?>>> captor = ArgumentCaptor.forClass(List.class);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testRoundingInvalidChromosome() throws InterruptedException, ExecutionException {
        SmodelBitset smodelBitset = new SmodelBitset(4);
        smodelBitset.set(3);
        smodelBitset.set(2);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(smodelBitset, optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(DELTA, fitnessEvaluator, normalizer,
                TOO_LONG_FLOATING_POINT_NUMBER);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, fitness, DELTA);
    }
}
