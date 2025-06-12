package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;

public class OptimizableChromosomeBinaryConstraintTest {
    private OptimizableChromosomeBinaryConstraint objectUnderTest;

    @Mock
    private Optimizable optimizable;
    @Mock
    private Chromosome<BitGene> chromosome;
    @Mock
    private Chromosome<BitGene> newChromosome;

    private Genotype<BitGene> genotype;
    private Phenotype<BitGene, Double> phenotype;

    @Before
    public void setUp() {
        initMocks(this);

        when(chromosome.isValid()).thenReturn(true);
        genotype = Genotype.of(chromosome);
        phenotype = Phenotype.of(genotype, 0);

        objectUnderTest = new OptimizableChromosomeBinaryConstraint();
    }

    @Test
    public void testValidPhenotype() {
        assertTrue(objectUnderTest.test(phenotype));
    }

    @Test
    public void testInvalidPhenotype() {
        when(chromosome.isValid()).thenReturn(false);

        assertFalse(objectUnderTest.test(phenotype));
    }

    @Test
    public void testRepairInvalidPhenotype() {
        when(chromosome.isValid()).thenReturn(false);
        when(newChromosome.isValid()).thenReturn(true);
        when(chromosome.newInstance()).thenReturn(newChromosome);

        Phenotype<BitGene, Double> actualInstance = objectUnderTest.repair(phenotype, 0);

        assertTrue(actualInstance.isValid());
        assertSame(newChromosome, actualInstance.genotype()
                .get(0));
    }

    @Test
    public void testRepairValidPhenotype() {
        Phenotype<BitGene, Double> actualInstance = objectUnderTest.repair(phenotype, 0);

        assertTrue(actualInstance.isValid());
        assertSame(chromosome, actualInstance.genotype()
            .get(0));
    }

}
