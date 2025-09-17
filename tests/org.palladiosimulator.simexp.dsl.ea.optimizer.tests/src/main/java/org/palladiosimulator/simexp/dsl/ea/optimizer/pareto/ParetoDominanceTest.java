package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.util.IntRange;

public class ParetoDominanceTest {
    private static final double EPSILON = 0.0001;

    private ParetoDominance<IntegerGene> paretoDominance;

    private IntRange range;

    @Mock
    private IAverageProvider<IntegerGene> averageProvider;
    @Mock
    private IPrecisionProvider precisionProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(precisionProvider.getPrecision()).thenReturn(EPSILON);

        range = IntRange.of(0, 10);

        paretoDominance = new ParetoDominance<>(precisionProvider, averageProvider, s -> Double::compare);
    }

    @Test
    public void testCompareEqual() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testComparePrecision() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2.0001, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2.0002, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testComparePareto() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(1, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 1));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 3));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNotDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 3));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    private Map<String, Double> buildAverages(double one, double two) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", one);
        averages.put("qa2", two);
        return averages;
    }

    private Phenotype<IntegerGene, Double> createPhenotype(int allele, double fitness) {
        IntegerGene gene = IntegerGene.of(allele, range);
        IntegerChromosome chromo = IntegerChromosome.of(gene);
        Genotype<IntegerGene> genoType = Genotype.of(chromo);
        Phenotype<IntegerGene, Double> phenoType = Phenotype.of(genoType, 0L, fitness);
        return phenoType;
    }
}
