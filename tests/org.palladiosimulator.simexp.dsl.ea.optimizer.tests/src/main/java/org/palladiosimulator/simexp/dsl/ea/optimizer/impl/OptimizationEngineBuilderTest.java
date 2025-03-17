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
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.ConfigHelper;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class OptimizationEngineBuilderTest {

    private Genotype<IntegerGene> genotype;

    private Phenotype<IntegerGene, Double> phenotype;

    @Mock
    private MOEAFitnessFunction fitnessFunction;

    @Mock
    private Executor executor;

    @Before
    public void setUp() {
        initMocks(this);
        genotype = Genotype.of(IntegerChromosome.of(IntRange.of(0, 5)));
        phenotype = Phenotype.of(genotype, 0);
    }

    @Test
    public void testBuildEngine() {
        // Arrange
        int populationSize = 500;
        IEAConfig config = new ConfigHelper(populationSize, 0.2, 0.5, 7, 50);
        EAOptimizationEngineBuilder optimizationEngineBuilder = new EAOptimizationEngineBuilder(config);
        ISeq<Phenotype<IntegerGene, Double>> phenoSeq = ISeq.of(phenotype);
        when(fitnessFunction.apply(ArgumentMatchers.any())).thenReturn(0.0);

        // Act
        Engine<IntegerGene, Double> engine = optimizationEngineBuilder.buildEngine(fitnessFunction, genotype,
                Runnable::run);

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
