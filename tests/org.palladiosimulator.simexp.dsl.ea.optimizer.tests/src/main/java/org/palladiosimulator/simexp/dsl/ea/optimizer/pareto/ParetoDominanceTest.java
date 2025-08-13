package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.util.IntRange;

public class ParetoDominanceTest {
    private ParetoDominance<IntegerGene> paretoDominance;

    private IntRange range;

    @Before
    public void setUp() throws Exception {
        range = IntRange.of(0, 10);

        paretoDominance = new ParetoDominance<>();
    }

    @Test
    public void testCompareEqual() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 2.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(1);
    }

    @Test
    public void testCompareNotDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 2.0);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(-1);
    }

    private Phenotype<IntegerGene, Double> createPhenotype(int allele, double fitness) {
        IntegerGene gene = IntegerGene.of(allele, range);
        IntegerChromosome chromo = IntegerChromosome.of(gene);
        Genotype<IntegerGene> genoType = Genotype.of(chromo);
        Phenotype<IntegerGene, Double> phenoType = Phenotype.of(genoType, 0L, fitness);
        return phenoType;
    }
}
