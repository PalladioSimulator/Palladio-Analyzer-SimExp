package org.palladiosimulator.simexp.dsl.ea.optimizer.operators;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

public class OHCrossoverTest {

    private OHCrossover crossover;

    @Before
    public void setUp() {
        crossover = new OHCrossover(1.0, 0.8);
    }

    @Test
    public void testCrossoverSmall() {
        SmodelBitset lowerBitset = new SmodelBitset(5);
        lowerBitset.set(0);
        SmodelBitset upperBitset = new SmodelBitset(5);
        upperBitset.set(4);
        BitChromosome lowerChromosome = BitChromosome.of(lowerBitset, 5);
        BitChromosome upperChromosome = BitChromosome.of(upperBitset, 5);
        List<BitGene> lowerList = extractBitGeneSeq(lowerChromosome);
        List<BitGene> upperList = extractBitGeneSeq(upperChromosome);
        MSeq<BitGene> lowerSeq = MSeq.of(lowerList);
        MSeq<BitGene> upperSeq = MSeq.of(upperList);
        Function<? super Random, Integer> function = r -> {
            return crossover.crossover(lowerSeq, upperSeq);
        };

        int crossoverNumberOfChanges = RandomRegistry.with(new Random(42), function);
        assertEquals(2, crossoverNumberOfChanges);

        assertEquals(List.of(false, false, true, false, false), lowerSeq.asList()
            .stream()
            .map(g -> g.bit())
            .toList());
        assertEquals(List.of(true, false, false, false, false), upperSeq.asList()
            .stream()
            .map(g -> g.bit())
            .toList());
    }

    @Test
    public void testCrossoverLong() {
        SmodelBitset lowerBitset = new SmodelBitset(20);
        lowerBitset.set(0);
        SmodelBitset upperBitset = new SmodelBitset(20);
        upperBitset.set(19);
        BitChromosome lowerChromosome = BitChromosome.of(lowerBitset, 20);
        BitChromosome upperChromosome = BitChromosome.of(upperBitset, 20);
        List<BitGene> lowerList = extractBitGeneSeq(lowerChromosome);
        List<BitGene> upperList = extractBitGeneSeq(upperChromosome);
        MSeq<BitGene> lowerSeq = MSeq.of(lowerList);
        MSeq<BitGene> upperSeq = MSeq.of(upperList);
        Function<? super Random, Integer> function = r -> {
            return crossover.crossover(lowerSeq, upperSeq);
        };

        int crossoverNumberOfChanges = RandomRegistry.with(new Random(42), function);
        assertEquals(2, crossoverNumberOfChanges);

        assertEquals(
                List.of(false, false, false, false, false, false, false, false, false, false, false, false, true, false,
                        false, false, false, false, false, false),
                lowerSeq.asList()
                    .stream()
                    .map(g -> g.bit())
                    .toList());
        assertEquals(
                List.of(false, false, false, false, false, false, true, false, false, false, false, false, false, false,
                        false, false, false, false, false, false),
                upperSeq.asList()
                    .stream()
                    .map(g -> g.bit())
                    .toList());
    }

    @Test
    public void testCrossoverOutside() {
        SmodelBitset lowerBitset = new SmodelBitset(10);
        lowerBitset.set(4);
        SmodelBitset upperBitset = new SmodelBitset(10);
        upperBitset.set(6);
        BitChromosome lowerChromosome = BitChromosome.of(lowerBitset, 10);
        BitChromosome upperChromosome = BitChromosome.of(upperBitset, 10);
        List<BitGene> lowerList = extractBitGeneSeq(lowerChromosome);
        List<BitGene> upperList = extractBitGeneSeq(upperChromosome);
        MSeq<BitGene> lowerSeq = MSeq.of(lowerList);
        MSeq<BitGene> upperSeq = MSeq.of(upperList);
        OHCrossover crossover = new OHCrossover(1.0, 0.0);
        Function<? super Random, Integer> function = r -> {
            return crossover.crossover(lowerSeq, upperSeq);
        };

        int crossoverNumberOfChanges = RandomRegistry.with(new Random(42), function);
        assertEquals(2, crossoverNumberOfChanges);

        assertEquals(List.of(false, false, false, true, false, false, false, false, false, false), lowerSeq.asList()
            .stream()
            .map(g -> g.bit())
            .toList());
        assertEquals(List.of(false, false, false, false, false, false, true, false, false, false), upperSeq.asList()
            .stream()
            .map(g -> g.bit())
            .toList());
    }

    @Test
    public void testCrossoverOutsideOverflow() {
        SmodelBitset lowerBitset = new SmodelBitset(10);
        lowerBitset.set(0);
        SmodelBitset upperBitset = new SmodelBitset(10);
        upperBitset.set(9);
        BitChromosome lowerChromosome = BitChromosome.of(lowerBitset, 10);
        BitChromosome upperChromosome = BitChromosome.of(upperBitset, 10);
        List<BitGene> lowerList = extractBitGeneSeq(lowerChromosome);
        List<BitGene> upperList = extractBitGeneSeq(upperChromosome);
        MSeq<BitGene> lowerSeq = MSeq.of(lowerList);
        MSeq<BitGene> upperSeq = MSeq.of(upperList);
        OHCrossover crossover = new OHCrossover(1.0, 0.0);
        Function<? super Random, Integer> function = r -> {
            return crossover.crossover(lowerSeq, upperSeq);
        };

        int crossoverNumberOfChanges = RandomRegistry.with(new Random(50), function);
        assertEquals(2, crossoverNumberOfChanges);

        assertEquals(List.of(false, false, false, false, false, false, false, false, true, false), lowerSeq.asList()
            .stream()
            .map(g -> g.bit())
            .toList());
        assertEquals(List.of(false, false, false, false, false, false, true, false, false, false), upperSeq.asList()
            .stream()
            .map(g -> g.bit())
            .toList());
    }

    private List<BitGene> extractBitGeneSeq(BitChromosome chromosome) {
        Iterator<BitGene> iterator = chromosome.iterator();
        List<BitGene> bitGeneList = new ArrayList();
        while (iterator.hasNext()) {
            bitGeneList.add(iterator.next());
        }
        return bitGeneList;
    }

}
