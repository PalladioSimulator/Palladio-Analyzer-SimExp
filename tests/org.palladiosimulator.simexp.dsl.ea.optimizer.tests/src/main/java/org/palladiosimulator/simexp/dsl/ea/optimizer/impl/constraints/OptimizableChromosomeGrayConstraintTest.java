package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Random;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.RandomRegistry;

public class OptimizableChromosomeGrayConstraintTest {

    @Mock
    private Optimizable optimizable;
    private OptimizableChromosomeGrayConstraint objectUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        objectUnderTest = new OptimizableChromosomeGrayConstraint();
    }

    @Test
    public void testValidPhenotype() {
        SmodelBitset bitSet = new SmodelBitset(4);
        bitSet.set(1);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitSet, optimizable, 5);
        Phenotype<BitGene, Double> phenotype = Phenotype.of(Genotype.of(chromosome), 0);

        boolean testResult = objectUnderTest.test(phenotype);

        assertTrue(testResult);
    }

    @Test
    public void testInvalidPhenotype() {
        SmodelBitset bitSet = new SmodelBitset(4);
        bitSet.set(1);
        bitSet.set(3);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitSet, optimizable, 5);
        Phenotype<BitGene, Double> phenotype = Phenotype.of(Genotype.of(chromosome), 0);

        boolean testResult = objectUnderTest.test(phenotype);

        assertFalse(testResult);
    }

    @Test
    public void testRepairPhenotype() {
        SmodelBitset bitSet = new SmodelBitset(4);
        bitSet.set(1);
        bitSet.set(3);
        SmodelBitset expectedBitset = new SmodelBitset(4);
        expectedBitset.set(0);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitSet, optimizable, 5);
        Phenotype<BitGene, Double> phenotype = Phenotype.of(Genotype.of(chromosome), 0);
        Function<? super Random, Phenotype<BitGene, Double>> optFunction = (Random r) -> {
            return objectUnderTest.repair(phenotype, 0);
        };

        Phenotype<BitGene, Double> newInstance = RandomRegistry.with(new Random(30), optFunction);

        assertTrue(objectUnderTest.test(newInstance));
        assertTrue(newInstance.isValid());
        assertEquals(expectedBitset, newInstance.genotype()
            .chromosome()
            .as(SmodelBitChromosome.class)
            .toBitSet());
    }

    @Test
    public void testRepairValidPhenotype() {
        SmodelBitset bitSet = new SmodelBitset(4);
        bitSet.set(0);
        bitSet.set(1);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitSet, optimizable, 5);
        Phenotype<BitGene, Double> phenotype = Phenotype.of(Genotype.of(chromosome), 0);
        Function<? super Random, Phenotype<BitGene, Double>> optFunction = (Random r) -> {
            return objectUnderTest.repair(phenotype, 0);
        };

        Phenotype<BitGene, Double> newInstance = RandomRegistry.with(new Random(30), optFunction);

        assertTrue(objectUnderTest.test(newInstance));
        assertTrue(newInstance.isValid());
        assertEquals(bitSet, newInstance.genotype()
            .chromosome()
            .as(SmodelBitChromosome.class)
            .toBitSet());
    }
}
