package org.palladiosimulator.simexp.dsl.ea.optimizer.moea;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;

public class FitnessFunctionTest {
    private static final double BIG_TOO_LONG_FLOATING_POINT_NUMBER = 50.12345678901234567890123456789;
    private static final double DELTA = 0.0001;
    private static final double TOO_LONG_FLOATING_POINT_NUMBER = 0.123456789;

    private FitnessFunction<BitGene> fitnessFunction;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;
    @Mock
    private ITranscoder<BitGene> normalizer;
    @Mock
    private Optimizable optimizable;
    @Mock
    private Future<Optional<Double>> fitnessFuture;
    @Mock
    private OptimizableValue<Double> optimizableValue;
    @Captor
    private ArgumentCaptor<List<OptimizableValue<?>>> captor;
    @Mock
    private BitChromosome chromosome;

    private Genotype<BitGene> genotype;

    @Before
    public void setUp() throws IOException, InterruptedException, ExecutionException {
        initMocks(this);

        when(chromosome.isValid()).thenReturn(true);
        genotype = Genotype.of(chromosome);

        when(normalizer.toOptimizableValues(genotype)).thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(ArgumentMatchers.any())).thenReturn(fitnessFuture);

        fitnessFunction = new FitnessFunction<>(DELTA, fitnessEvaluator, normalizer, 0.0);
    }

    @Test
    public void testApply() throws IOException, InterruptedException, ExecutionException {
        when(fitnessFuture.get()).thenReturn(Optional.of(50.0));

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(50.0, actualFitness, DELTA);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testHandleInterruptedException() throws InterruptedException, ExecutionException, IOException {
        when(fitnessFuture.get()).thenThrow(new InterruptedException("This is a test"));

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, actualFitness, DELTA);
    }

    @Test
    public void testHandleExecutionException() throws InterruptedException, ExecutionException, IOException {
        when(fitnessFuture.get()).thenThrow(new ExecutionException("", null));

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, actualFitness, DELTA);
    }

    @Test
    public void testInvalidChromosome() throws InterruptedException, ExecutionException, IOException {
        when(chromosome.isValid()).thenReturn(false);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.0, actualFitness, DELTA);
    }

    @Test
    public void testRoundingApply() throws IOException, InterruptedException, ExecutionException {
        when(fitnessFuture.get()).thenReturn(Optional.of(50.12345678901234567890123456789));

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(50.1235, actualFitness, DELTA);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testRoundingApplyMimimumDelta() throws IOException, InterruptedException, ExecutionException {
        when(fitnessFuture.get()).thenReturn(Optional.of(BIG_TOO_LONG_FLOATING_POINT_NUMBER));
        double smallEpsilon = 0.0000000000001;
        fitnessFunction = new FitnessFunction<>(smallEpsilon, fitnessEvaluator, normalizer, 0.0);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(50.12345678901234, actualFitness, smallEpsilon);
        verify(fitnessEvaluator).calcFitness(captor.capture());
        assertTrue(captor.getAllValues()
            .get(0)
            .contains(optimizableValue));
    }

    @Test
    public void testRoundingInvalidChromosome() throws InterruptedException, ExecutionException, IOException {
        when(chromosome.isValid()).thenReturn(false);
        fitnessFunction = new FitnessFunction<>(DELTA, fitnessEvaluator, normalizer, TOO_LONG_FLOATING_POINT_NUMBER);

        double actualFitness = fitnessFunction.apply(genotype);

        assertEquals(0.1235, actualFitness, DELTA);
    }
}
