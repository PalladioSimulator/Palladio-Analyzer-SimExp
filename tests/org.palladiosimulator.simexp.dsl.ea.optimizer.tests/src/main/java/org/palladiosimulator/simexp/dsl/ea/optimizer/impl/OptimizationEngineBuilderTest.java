package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.util.ISeq;

public class OptimizationEngineBuilderTest {

    private Genotype<BitGene> genotype;

    private Phenotype<BitGene, Double> phenotype;

    @Mock
    private FitnessFunction fitnessFunction;

    @Mock
    private Executor executor;

    @Before
    public void setUp() {
        initMocks(this);
        genotype = Genotype.of(BitChromosome.of(5));
        phenotype = Phenotype.of(genotype, 0);
    }

    @Test
    public void testBuildEngine() {
        // Arrange
        OptimizationEngineBuilder optimizationEngineBuilder = new OptimizationEngineBuilder();
        int populationSize = 500;
        int selectorSize = 5;
        int offspringSelectorSize = 5;
        double mutationRate = 0.2;
        double crossoverRate = 0.5;
        ISeq<Phenotype<BitGene, Double>> phenoSeq = ISeq.of(phenotype);
        when(fitnessFunction.apply(ArgumentMatchers.any())).thenReturn(0.0);

        // Act
        Engine<BitGene, Double> engine = optimizationEngineBuilder.buildEngine(fitnessFunction, genotype,
                populationSize, Runnable::run, selectorSize, offspringSelectorSize, mutationRate, crossoverRate);

        // Assert
        engine.eval(phenoSeq);
        verify(fitnessFunction).apply(genotype);
        assertEquals(populationSize, engine.populationSize());
        assertEquals(TournamentSelector.class, engine.survivorsSelector()
            .getClass());
        assertEquals(TournamentSelector.class, engine.offspringSelector()
            .getClass());
    }
}
