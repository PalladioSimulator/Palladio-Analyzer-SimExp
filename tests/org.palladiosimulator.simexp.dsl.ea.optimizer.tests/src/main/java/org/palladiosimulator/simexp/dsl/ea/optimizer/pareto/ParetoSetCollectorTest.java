package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

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
    @Mock
    private IPrecisionProvider precisionProvider;

    private Phenotype<IntegerGene, Double> a;
    private Phenotype<IntegerGene, Double> b;
    private Phenotype<IntegerGene, Double> c;
    private Phenotype<IntegerGene, Double> d;
    private Phenotype<IntegerGene, Double> e;
    private Phenotype<IntegerGene, Double> f;
    private Phenotype<IntegerGene, Double> g;
    private Phenotype<IntegerGene, Double> h;
    private Phenotype<IntegerGene, Double> i;
    private Phenotype<IntegerGene, Double> j;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(precisionProvider.getPrecision()).thenReturn(EPSILON);

        range = IntRange.of(0, 10);

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
        a = createPhenotype(0, 1.0);
        b = createPhenotype(1, 1.0);
        c = createPhenotype(2, 1.0);
        d = createPhenotype(3, 1.0);
        e = createPhenotype(4, 1.0);
        f = createPhenotype(5, 1.0);
        g = createPhenotype(6, 1.0);
        h = createPhenotype(7, 1.0);
        i = createPhenotype(8, 1.0);
        j = createPhenotype(9, 1.0);

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

        collector = ParetoSetCollector.create(precisionProvider, averageProvider, s -> Double::compare);
    }

    @Test
    public void paretoFrontMinimization() {
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
        Stream<EvolutionResult<IntegerGene, Double>> resultStream = buildResultStream(Optimize.MINIMUM);
        ISeq<Phenotype<IntegerGene, Double>> actualResult = resultStream.collect(collector);

        assertThat(actualResult).containsExactlyInAnyOrder(a, h, j, g);
    }

    @Test
    public void paretoFrontMaximization() {
        // y\x -0- -1- -2- -3- -4- -5- -6- -7-
        // -----------------------------------
        // 8 | [J] --- --- --- --- --- --- ---
        // 7 | --- [A] --- --- --- --- --- ---
        // 6 | --- --- [B] --- --- --- --- ---
        // 5 | --- --- --- -C- --- [I] --- ---
        // 4 | --- --- --- --- -D- --- --- ---
        // 3 | --- --- --- --- --- -E- --- ---
        // 2 | --- --- -H- --- --- --- [F] ---
        // 1 | --- --- --- --- --- --- --- [G]
        // -----------------------------------
        // ___ -0- -1- -2- -3- -4- -5- -6- -7-
        //
        // Pareto front (non-dominated points, maximization):
        // A(1.0, 7.0)
        // B(2.0, 6.0)
        // I(5.0, 5.0)
        // F(6.0, 2.0)
        // G(7.0, 1.0)
        // J(0.0, 8.0)
        Stream<EvolutionResult<IntegerGene, Double>> resultStream = buildResultStream(Optimize.MAXIMUM);
        ISeq<Phenotype<IntegerGene, Double>> actualResult = resultStream.collect(collector);

        assertThat(actualResult).containsExactlyInAnyOrder(a, b, i, f, g, j);
    }

    private Stream<EvolutionResult<IntegerGene, Double>> buildResultStream(Optimize optimize) {
        EvolutionResult<IntegerGene, Double> ra = createEvolutionResult(a, optimize);
        EvolutionResult<IntegerGene, Double> rb = createEvolutionResult(b, optimize);
        EvolutionResult<IntegerGene, Double> rc = createEvolutionResult(c, optimize);
        EvolutionResult<IntegerGene, Double> rd = createEvolutionResult(d, optimize);
        EvolutionResult<IntegerGene, Double> re = createEvolutionResult(e, optimize);
        EvolutionResult<IntegerGene, Double> rf = createEvolutionResult(f, optimize);
        EvolutionResult<IntegerGene, Double> rg = createEvolutionResult(g, optimize);
        EvolutionResult<IntegerGene, Double> rh = createEvolutionResult(h, optimize);
        EvolutionResult<IntegerGene, Double> ri = createEvolutionResult(i, optimize);
        EvolutionResult<IntegerGene, Double> rj = createEvolutionResult(j, optimize);
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

        Stream<EvolutionResult<IntegerGene, Double>> resultStream = Stream.of(ra, rb, rc, rd, re, rf, rg, rh, ri, rj);
        return resultStream;
    }

    private Optional<Map<String, Double>> buildAverages(double one, double two) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", one);
        averages.put("qa2", two);
        return Optional.of(averages);
    }

    private EvolutionResult<IntegerGene, Double> createEvolutionResult(Phenotype<IntegerGene, Double> phenoType,
            Optimize optimize) {
        EvolutionResult<IntegerGene, Double> er = EvolutionResult.of(optimize, ISeq.of(phenoType), 1L,
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
