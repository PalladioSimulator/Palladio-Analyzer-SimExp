package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.util.IntRange;

public class ParetoDominanceTest {
    private ParetoDominance<IntegerGene> paretoDominance;

    private IntRange range;

    @Mock
    private IAverageProvider<IntegerGene> averageProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        range = IntRange.of(0, 10);

        paretoDominance = new ParetoDominance<>(averageProvider);
    }

    @Test
    public void testCompareEqual() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", 2.0);
        averagesA.put("qa2", 2.0);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", 2.0);
        averagesB.put("qa2", 2.0);
        when(averageProvider.getAverages(a)).thenReturn(averagesA);
        when(averageProvider.getAverages(b)).thenReturn(averagesB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testComparePareto() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", 1.0);
        averagesA.put("qa2", 2.0);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", 2.0);
        averagesB.put("qa2", 1.0);
        when(averageProvider.getAverages(a)).thenReturn(averagesA);
        when(averageProvider.getAverages(b)).thenReturn(averagesB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", 2.0);
        averagesA.put("qa2", 2.0);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", 2.0);
        averagesB.put("qa2", 3.0);
        when(averageProvider.getAverages(a)).thenReturn(averagesA);
        when(averageProvider.getAverages(b)).thenReturn(averagesB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNotDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", 2.0);
        averagesA.put("qa2", 3.0);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", 2.0);
        averagesB.put("qa2", 2.0);
        when(averageProvider.getAverages(a)).thenReturn(averagesA);
        when(averageProvider.getAverages(b)).thenReturn(averagesB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    private Phenotype<IntegerGene, Double> createPhenotype(int allele, double fitness) {
        IntegerGene gene = IntegerGene.of(allele, range);
        IntegerChromosome chromo = IntegerChromosome.of(gene);
        Genotype<IntegerGene> genoType = Genotype.of(chromo);
        Phenotype<IntegerGene, Double> phenoType = Phenotype.of(genoType, 0L, fitness);
        return phenoType;
    }
}
