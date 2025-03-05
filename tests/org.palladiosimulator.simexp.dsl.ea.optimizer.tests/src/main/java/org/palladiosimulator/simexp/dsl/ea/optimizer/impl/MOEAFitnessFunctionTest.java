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
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.IntRange;

public class MOEAFitnessFunctionTest {

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
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(fitnessEvaluator, normalizer);

        // Act
        Vec<double[]> fitness = fitnessFunction.apply(genotype);

        // Assert
        assertEquals(50.0, fitness.data()[0], 0.00001);
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
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(fitnessEvaluator, normalizer);

        Vec<double[]> fitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, fitness.data()[0], 0.00001);
    }

    @Test
    public void testHandleExecutionException() throws InterruptedException, ExecutionException {
        SmodelIntegerChromosome chromosome = SmodelIntegerChromosome.of(IntRange.of(0, 3), optimizable);
        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(fitnessEvaluator, normalizer);

        Vec<double[]> fitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, fitness.data()[0], 0.00001);
    }

//    @Test
//    public void testBigChromosome() throws InterruptedException, ExecutionException {
//        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
//            return SmodelIntegerChromosome.of(IntRange.of(0, 100), optimizable);
//        };
//        SmodelIntegerChromosome chromosome = RandomRegistry.with(new Random(42), func);
//        Genotype<IntegerGene> genotype = Genotype.of(chromosome);
//        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
//        when(fitnessFuture.get()).thenThrow(executionException);
//        MOEAFitnessFunction fitnessFunction = new MOEAFitnessFunction(fitnessEvaluator, normalizer);
//
//        Vec<double[]> fitness = fitnessFunction.apply(genotype);
//
//        assertEquals(0.0, fitness.data()[0], 0.00001);
//    }

}
