package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.BinaryBitInterpreter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;

public class EAFitnessFunctionTest {
    private static final double BIG_TOO_LONG_FLOATING_POINT_NUMBER = 50.12345678901234567890123456789;

    private static final double DELTA = 0.0001;

    private static final double TOO_LONG_FLOATING_POINT_NUMBER = 0.123456789;

    @Mock
    IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    OptimizableNormalizer normalizer;

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
    public void testApply() throws IOException {
        // Arrange
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
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
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

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
    public void testHandleInterruptedException() throws InterruptedException, ExecutionException, IOException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(new InterruptedException("This is a test"));
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer,
                TOO_LONG_FLOATING_POINT_NUMBER);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, fitness, 0.00001);
    }

    @Test
    public void testHandleExecutionException() throws InterruptedException, ExecutionException, IOException {
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, fitness, DELTA);
    }

    @Test
    public void testInvalidChromosome() throws InterruptedException, ExecutionException, IOException {
        SmodelBitset smodelBitset = new SmodelBitset(4);
        smodelBitset.set(3);
        smodelBitset.set(2);
        SmodelBitChromosome chromosome = createSmodelBitChromosomeFromSmodelBitset(smodelBitset);
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, fitness, DELTA);
    }

    @Test
    public void testRoundingApply() throws IOException {
        // Arrange
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
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
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer);

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
    public void testRoundingApplyMimimumDelta() throws IOException {
        // Arrange
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4,
                new BinaryBitInterpreter());
        Genotype<BitGene> genotype = Genotype.of(chromosome);
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
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(smallEpsilon, fitnessEvaluator, normalizer);

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
    public void testRoundingInvalidChromosome() throws InterruptedException, ExecutionException, IOException {
        SmodelBitset smodelBitset = new SmodelBitset(4);
        smodelBitset.set(3);
        smodelBitset.set(2);
        SmodelBitChromosome chromosome = createSmodelBitChromosomeFromSmodelBitset(smodelBitset);
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        when(fitnessFuture.get()).thenThrow(executionException);
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer,
                TOO_LONG_FLOATING_POINT_NUMBER);

        Double fitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, fitness, DELTA);
    }

    @Test
    public void testGetNumberOfUniqueFitnessEvaluationsWithTwoUniqueGenotypes() throws IOException {
        // Arrange
        SmodelBitset firstSmodelBitset = new SmodelBitset(2);
        SmodelBitset secondSmodelBitset = new SmodelBitset(2);
        secondSmodelBitset.set(0);
        SmodelBitset thirdSmodelBitset = new SmodelBitset(2);
        thirdSmodelBitset.set(1);
        SmodelBitset fourthSmodelBitset = new SmodelBitset(2);
        fourthSmodelBitset.set(1);
        fourthSmodelBitset.set(0);

        SmodelBitChromosome firstChromosome = createSmodelBitChromosomeFromSmodelBitset(firstSmodelBitset);
        SmodelBitChromosome secondChromosome = createSmodelBitChromosomeFromSmodelBitset(secondSmodelBitset);
        SmodelBitChromosome thirdChromosome = createSmodelBitChromosomeFromSmodelBitset(thirdSmodelBitset);
        SmodelBitChromosome fourthChromosome = createSmodelBitChromosomeFromSmodelBitset(fourthSmodelBitset);

        Genotype<BitGene> firstGenotype = Genotype.of(firstChromosome, secondChromosome);
        Genotype<BitGene> secondGenotype = Genotype.of(thirdChromosome, fourthChromosome);

        OptimizableValue<Double> firstOptimizableValue = mock(OptimizableValue.class);
        OptimizableValue<Double> secondOptimizableValue = mock(OptimizableValue.class);
        OptimizableValue<Double> thirdOptimizableValue = mock(OptimizableValue.class);
        OptimizableValue<Double> fourthOptimizableValue = mock(OptimizableValue.class);

        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s != null && s.contains(firstChromosome))))
            .thenReturn(List.of(firstOptimizableValue, secondOptimizableValue));
        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s != null && s.contains(thirdChromosome))))
            .thenReturn(List.of(thirdOptimizableValue, fourthOptimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        try {
            when(fitnessFuture.get()).thenReturn(Optional.of(50.12345678901234567890123456789));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer);
        fitnessFunction.apply(firstGenotype);
        fitnessFunction.apply(secondGenotype);

        // Act
        long numberOfUniqueFitnessEvaluations = fitnessFunction.getNumberOfUniqueFitnessEvaluations();

        assertEquals(2, numberOfUniqueFitnessEvaluations);
    }

    @Test
    public void testGetNumberOfUniqueFitnessEvaluationsWithTwoEqualGenotypes() throws IOException {
        // Arrange
        SmodelBitset firstSmodelBitset = new SmodelBitset(2);
        SmodelBitset secondSmodelBitset = new SmodelBitset(2);
        secondSmodelBitset.set(0);

        SmodelBitChromosome firstChromosome = createSmodelBitChromosomeFromSmodelBitset(firstSmodelBitset);
        SmodelBitChromosome secondChromosome = createSmodelBitChromosomeFromSmodelBitset(secondSmodelBitset);

        Genotype<BitGene> firstGenotype = Genotype.of(firstChromosome, secondChromosome);
        Genotype<BitGene> secondGenotype = Genotype.of(firstChromosome, secondChromosome);

        OptimizableValue<Double> firstOptimizableValue = mock(OptimizableValue.class);
        OptimizableValue<Double> secondOptimizableValue = mock(OptimizableValue.class);

        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s != null && s.contains(firstChromosome))))
            .thenReturn(List.of(firstOptimizableValue, secondOptimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);
        try {
            when(fitnessFuture.get()).thenReturn(Optional.of(50.12345678901234567890123456789));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
        EAFitnessFunction fitnessFunction = new EAFitnessFunction(DELTA, fitnessEvaluator, normalizer);
        fitnessFunction.apply(firstGenotype);
        fitnessFunction.apply(secondGenotype);

        // Act
        long numberOfUniqueFitnessEvaluations = fitnessFunction.getNumberOfUniqueFitnessEvaluations();

        assertEquals(1, numberOfUniqueFitnessEvaluations);
    }

    private SmodelBitChromosome createSmodelBitChromosomeFromSmodelBitset(SmodelBitset firstSmodelBitset) {
        return SmodelBitChromosome.of(firstSmodelBitset, optimizable, 4, new BinaryBitInterpreter());
    }
}
