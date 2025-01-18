package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;

public class FitnessFunctionTest {

    @Mock
    IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    OptimizableNormalizer normalizer;

    @Mock
    Optimizable optimizable;

    @Mock
    Future<Double> fitnessFuture;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApply() {
        // Arrange
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(new SmodelBitset(3), optimizable, 4);
        Genotype<BitGene> genotype = Genotype.of(chromosome);
        OptimizableValue<Double> optimizableValue = mock(OptimizableValue.class);
        when(normalizer.toOptimizableValues(Mockito.argThat(s -> s.contains(chromosome))))
            .thenReturn(List.of(optimizableValue));
        when(fitnessEvaluator.calcFitness(Mockito.argThat(s -> s.contains(optimizableValue))))
            .thenReturn(fitnessFuture);
        try {
            when(fitnessFuture.get()).thenReturn(50.0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
        FitnessFunction fitnessFunction = new FitnessFunction(fitnessEvaluator, normalizer);

        // Act
        Double fitness = fitnessFunction.apply(genotype);

        // Assert
        assertEquals((Double) 50.0, fitness);
        verify(fitnessEvaluator.calcFitness(Mockito.argThat(s -> s.contains(optimizableValue))));
    }

}
