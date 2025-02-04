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
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.BinaryBitInterpreter;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;

public class OptimizationEngineBuilderTest {

    private Genotype<BitGene> genotype;

    private Phenotype<BitGene, Vec<double[]>> phenotype;

    @Mock
    private MOEAFitnessFunction fitnessFunction;

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
        EAOptimizationEngineBuilder optimizationEngineBuilder = new EAOptimizationEngineBuilder();
        int populationSize = 500;
        int selectorSize = 5;
        int offspringSelectorSize = 5;
        double mutationRate = 0.2;
        double crossoverRate = 0.5;
        ISeq<Phenotype<BitGene, Vec<double[]>>> phenoSeq = ISeq.of(phenotype);
        double[] returnArray = { 0.0 };
        when(fitnessFunction.apply(ArgumentMatchers.any())).thenReturn(Vec.of(returnArray));

        // Act
        Engine<BitGene, Vec<double[]>> engine = optimizationEngineBuilder.buildEngine(fitnessFunction, genotype,
                new BinaryBitInterpreter(), populationSize, Runnable::run, selectorSize, offspringSelectorSize,
                mutationRate, crossoverRate);

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
