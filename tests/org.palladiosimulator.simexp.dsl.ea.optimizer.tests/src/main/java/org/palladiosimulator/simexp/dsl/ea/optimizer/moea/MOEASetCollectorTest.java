package org.palladiosimulator.simexp.dsl.ea.optimizer.moea;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionDurations;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class MOEASetCollectorTest {
    private Collector<EvolutionResult<IntegerGene, Double>, ?, ISeq<Phenotype<IntegerGene, Double>>> collector;

    private IntRange range;

    @Before
    public void setUp() throws Exception {
        range = IntRange.of(0, 100);

        collector = MOEASetCollector.create();
    }

    @Test
    public void collecSingleBest() {
        Phenotype<IntegerGene, Double> pheno1 = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> pheno2 = createPhenotype(2, 2.0);
        EvolutionResult<IntegerGene, Double> r1 = createEvolutionResult(pheno1);
        EvolutionResult<IntegerGene, Double> r2 = createEvolutionResult(pheno2);

        ISeq<Phenotype<IntegerGene, Double>> actualResult = Stream.of(r1, r2)
            .collect(collector);

        assertThat(actualResult).contains(createPhenotype(2, 2.0));
    }

    @Test
    public void collectDoubleBest() {
        Phenotype<IntegerGene, Double> pheno1 = createPhenotype(1, 2.0);
        Phenotype<IntegerGene, Double> pheno2 = createPhenotype(2, 2.0);
        EvolutionResult<IntegerGene, Double> r1 = createEvolutionResult(pheno1);
        EvolutionResult<IntegerGene, Double> r2 = createEvolutionResult(pheno2);

        ISeq<Phenotype<IntegerGene, Double>> actualResult = Stream.of(r1, r2)
            .collect(collector);

        assertThat(actualResult).contains(pheno1, pheno2);
    }

    @Test
    public void collectMaxMin1() {
        List<Phenotype<IntegerGene, Double>> phenotypes = new ArrayList<>();
        List<EvolutionResult<IntegerGene, Double>> evolutionResults = new ArrayList<>();
        for (int i = 0; i < MOEASetCollector.SIZE_MAX - 1; ++i) {
            Phenotype<IntegerGene, Double> phenotype = createPhenotype(i, 2.0);
            phenotypes.add(phenotype);
            evolutionResults.add(createEvolutionResult(phenotype));
        }

        ISeq<Phenotype<IntegerGene, Double>> actualResult = evolutionResults.stream()
            .collect(collector);

        assertThat(actualResult).containsAll(phenotypes);
    }

    @Test
    public void collectMax() {
        List<Phenotype<IntegerGene, Double>> phenotypes = new ArrayList<>();
        List<EvolutionResult<IntegerGene, Double>> evolutionResults = new ArrayList<>();
        for (int i = 0; i < MOEASetCollector.SIZE_MAX; ++i) {
            Phenotype<IntegerGene, Double> phenotype = createPhenotype(i, 2.0);
            phenotypes.add(phenotype);
            evolutionResults.add(createEvolutionResult(phenotype));
        }

        ISeq<Phenotype<IntegerGene, Double>> actualResult = evolutionResults.stream()
            .collect(collector);

        assertThat(actualResult).contains(phenotypes.get(0), phenotypes.get(8), phenotypes.get(9));
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
