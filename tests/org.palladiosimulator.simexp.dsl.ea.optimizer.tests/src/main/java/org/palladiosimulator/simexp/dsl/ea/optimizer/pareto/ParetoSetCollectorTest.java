package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionDurations;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class ParetoSetCollectorTest {
    private static final double EPSILON = 0.0001;

    private Collector<EvolutionResult<IntegerGene, Double>, ?, ISeq<Phenotype<IntegerGene, Double>>> collector;

    private IntRange range;

    @Mock
    private IAverageProvider<IntegerGene> averageProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        range = IntRange.of(0, 10);

        collector = ParetoSetCollector.create(EPSILON, averageProvider, s -> Double::compare);
    }

    @Test
    public void collectFront() {
        // All points:
        // A(1.0, 7.0)
        // B(2.0, 6.0)
        // C(3.0, 5.0)
        // D(4.0, 4.0)
        // E(5.0, 3.0)
        // F(6.0, 2.0)
        // G(7.0, 1.0)
        // H(2.0, 2.0)
        // I(5.0, 5.0)
        // J(0.0, 8.0)
        //
        // y\x -0- -1- -2- -3- -4- -5- -6- -7-
        // -----------------------------------
        // 8 | [J] --- --- --- --- --- --- ---
        // 7 | --- [A] --- --- --- --- --- ---
        // 6 | --- --- -B- --- --- --- --- ---
        // 5 | --- --- --- -C- --- -I- --- ---
        // 4 | --- --- --- --- -D- --- --- ---
        // 3 | --- --- --- --- --- -E- --- ---
        // 2 | --- --- [H] --- --- --- -F- ---
        // 1 | --- --- --- --- --- --- --- [G]
        // -----------------------------------
        // ___ -0- -1- -2- -3- -4- -5- -6- -7-
        //
        // Pareto front (non-dominated points, minimization):
        // A(1.0, 7.0)
        // J(0.0, 8.0)
        // H(2.0, 2.0)
        // G(7.0, 1.0)
        Phenotype<IntegerGene, Double> a = createPhenotype(0, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> c = createPhenotype(2, 1.0);
        Phenotype<IntegerGene, Double> d = createPhenotype(3, 1.0);
        Phenotype<IntegerGene, Double> e = createPhenotype(4, 1.0);
        Phenotype<IntegerGene, Double> f = createPhenotype(5, 1.0);
        Phenotype<IntegerGene, Double> g = createPhenotype(6, 1.0);
        Phenotype<IntegerGene, Double> h = createPhenotype(7, 1.0);
        Phenotype<IntegerGene, Double> i = createPhenotype(8, 1.0);
        Phenotype<IntegerGene, Double> j = createPhenotype(9, 1.0);
        EvolutionResult<IntegerGene, Double> ra = createEvolutionResult(a);
        EvolutionResult<IntegerGene, Double> rb = createEvolutionResult(b);
        EvolutionResult<IntegerGene, Double> rc = createEvolutionResult(c);
        EvolutionResult<IntegerGene, Double> rd = createEvolutionResult(d);
        EvolutionResult<IntegerGene, Double> re = createEvolutionResult(e);
        EvolutionResult<IntegerGene, Double> rf = createEvolutionResult(f);
        EvolutionResult<IntegerGene, Double> rg = createEvolutionResult(g);
        EvolutionResult<IntegerGene, Double> rh = createEvolutionResult(h);
        EvolutionResult<IntegerGene, Double> ri = createEvolutionResult(i);
        EvolutionResult<IntegerGene, Double> rj = createEvolutionResult(j);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(1, 7));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 6));
        when(averageProvider.getAverages(c)).thenReturn(buildAverages(3, 5));
        when(averageProvider.getAverages(d)).thenReturn(buildAverages(4, 4));
        when(averageProvider.getAverages(e)).thenReturn(buildAverages(5, 3));
        when(averageProvider.getAverages(f)).thenReturn(buildAverages(6, 2));
        when(averageProvider.getAverages(g)).thenReturn(buildAverages(7, 1));
        when(averageProvider.getAverages(h)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(i)).thenReturn(buildAverages(5, 5));
        when(averageProvider.getAverages(j)).thenReturn(buildAverages(0, 8));

        ISeq<Phenotype<IntegerGene, Double>> actualResult = Stream.of(ra, rb, rc, rd, re, rf, rg, rh, ri, rj)
            .collect(collector);

        assertThat(actualResult).containsExactlyInAnyOrder(a, h, j, g);
    }

    private Map<String, Double> buildAverages(double one, double two) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", one);
        averages.put("qa2", two);
        return averages;
    }

    private EvolutionResult<IntegerGene, Double> createEvolutionResult(Phenotype<IntegerGene, Double> phenoType) {
        EvolutionResult<IntegerGene, Double> er = EvolutionResult.of(Optimize.MAXIMUM, ISeq.of(phenoType), 1L,
                EvolutionDurations.ZERO, 0, 0, 0);
        return er;
    }

    private Phenotype<IntegerGene, Double> createPhenotype(int allele, double fitness) {
        IntegerGene gene = IntegerGene.of(allele, range);
        IntegerChromosome chromo = IntegerChromosome.of(gene);
        Genotype<IntegerGene> genoType = Genotype.of(chromo);
        Phenotype<IntegerGene, Double> phenoType = Phenotype.of(genoType, 0L, fitness);
        return phenoType;
    }
}
