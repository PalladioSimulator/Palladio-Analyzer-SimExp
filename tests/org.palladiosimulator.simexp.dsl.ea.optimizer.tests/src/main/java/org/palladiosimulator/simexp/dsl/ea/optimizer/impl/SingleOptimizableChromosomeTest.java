package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;

public class SingleOptimizableChromosomeTest {

    @Mock
    private Function<Genotype<BitGene>, Double> decoder;

    private Genotype<BitGene> genotype;

    @Mock
    private Optimizable optimizable;

    private Double returnObject;

    private SingleOptimizableChromosome<BitGene> chromosome;

    @Before
    public void setUp() {
        initMocks(this);
        genotype = Genotype.of(BitChromosome.of(5));
        returnObject = 5.0;
        when(decoder.apply(genotype)).thenReturn(returnObject);
        chromosome = new SingleOptimizableChromosome<>(decoder, genotype, optimizable);
    }

    @Test
    public void getPhenotypeTest() {
        Object phenotype = chromosome.getPhenotype();

        assertTrue(phenotype instanceof Double);
        assertEquals(returnObject, phenotype);
    }

    @Test
    public void toOptimizableValueTest() {
        OptimizableValue<?> optimizableValue = chromosome.toOptimizableValue();

        assertEquals(optimizable, optimizableValue.getOptimizable());
        Object value = optimizableValue.getValue();
        assertTrue(value instanceof Double);
        assertEquals(returnObject, value);
    }

}
