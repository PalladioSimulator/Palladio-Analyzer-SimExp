package org.palladiosimulator.simexp.dsl.ea.optimizer.operators;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.OneHotBitInterpreter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.MutatorResult;

public class OHMutatorTest {

    @Mock
    private Optimizable optimizable;

    @Mock
    private Random random;

    private OHMutator ohMutator;

    @Before
    public void setUp() {
        ohMutator = new OHMutator(1.0);
    }

    @Test
    public void testMutateChromosome() {
        SmodelBitset smodelBitset = new SmodelBitset(3);
        smodelBitset.set(1);
        SmodelBitset expectedMutatedBitset = new SmodelBitset(3);
        expectedMutatedBitset.set(2);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(smodelBitset, optimizable, 3,
                new OneHotBitInterpreter());

        MutatorResult<Chromosome<BitGene>> mutationResult = ohMutator.mutate(chromosome, 1.0, new Random(42));

        SmodelBitChromosome mutatedChromosome = mutationResult.result()
            .as(SmodelBitChromosome.class);
        assertEquals(expectedMutatedBitset, mutatedChromosome.toBitSet());
    }

    @Test
    public void testMutateLongChromosome() {
        SmodelBitset smodelBitset = new SmodelBitset(50);
        smodelBitset.set(49);
        SmodelBitset expectedMutatedBitset = new SmodelBitset(3);
        expectedMutatedBitset.set(2);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(smodelBitset, optimizable, 50,
                new OneHotBitInterpreter());

        MutatorResult<Chromosome<BitGene>> mutationResult = ohMutator.mutate(chromosome, 1.0, new Random(42));

        SmodelBitChromosome mutatedChromosome = mutationResult.result()
            .as(SmodelBitChromosome.class);
        assertEquals(expectedMutatedBitset, mutatedChromosome.toBitSet());
    }

}
