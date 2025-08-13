package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;

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
    private ITranscoder<IntegerGene> normalizer;
    @Mock
    private IQualityAttributeProvider qualityAttributeProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        range = IntRange.of(0, 100);

        collector = ParetoSetCollector.create(EPSILON, normalizer, qualityAttributeProvider);
    }

    // TODO
    @Ignore
    @Test
    public void collectSingleBest() {
        Phenotype<IntegerGene, Double> pheno1 = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> pheno2 = createPhenotype(2, 2.0);
        EvolutionResult<IntegerGene, Double> r1 = createEvolutionResult(pheno1);
        EvolutionResult<IntegerGene, Double> r2 = createEvolutionResult(pheno2);

        ISeq<Phenotype<IntegerGene, Double>> actualResult = Stream.of(r1, r2)
            .collect(collector);

        assertThat(actualResult).containsExactly(createPhenotype(2, 2.0));
    }

    // TODO
    @Ignore
    @Test
    public void collectDoubleBest() {
        Phenotype<IntegerGene, Double> pheno1 = createPhenotype(1, 2.0);
        Phenotype<IntegerGene, Double> pheno2 = createPhenotype(2, 2.0);
        EvolutionResult<IntegerGene, Double> r1 = createEvolutionResult(pheno1);
        EvolutionResult<IntegerGene, Double> r2 = createEvolutionResult(pheno2);

        ISeq<Phenotype<IntegerGene, Double>> actualResult = Stream.of(r1, r2)
            .collect(collector);

        assertThat(actualResult).containsExactly(pheno1, pheno2);
    }

    // TODO
    @Ignore
    @Test
    public void collectPrecision() {
        Phenotype<IntegerGene, Double> pheno1 = createPhenotype(1, 0.0001);
        Phenotype<IntegerGene, Double> pheno2 = createPhenotype(2, 0.0002);
        EvolutionResult<IntegerGene, Double> r1 = createEvolutionResult(pheno1);
        EvolutionResult<IntegerGene, Double> r2 = createEvolutionResult(pheno2);

        ISeq<Phenotype<IntegerGene, Double>> actualResult = Stream.of(r1, r2)
            .collect(collector);

        assertThat(actualResult).containsExactly(pheno1, pheno2);
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
