package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.Run;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.util.IntRange;

public class ParetoDominanceTest {
    private ParetoDominance<IntegerGene> paretoDominance;

    private IntRange range;

    private SmodelCreator smodelCreator;
    private Optimizable optimizable1;

    @Mock
    private ITranscoder<IntegerGene> normalizer;
    @Mock
    private IQualityAttributeProvider qualityAttributeProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        range = IntRange.of(0, 10);

        smodelCreator = new SmodelCreator();
        IntLiteral start = smodelCreator.createIntLiteral(range.min());
        IntLiteral end = smodelCreator.createIntLiteral(range.max());
        IntLiteral step = smodelCreator.createIntLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBoundsClosedClosed(start, end, step);
        optimizable1 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);

        paretoDominance = new ParetoDominance<>(normalizer, qualityAttributeProvider);
    }

    @Test
    public void testCompareEqual() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        OptimizableValue<Integer> optimizableValueA = new OptimizableValue<>(optimizable1, a.genotype()
            .gene()
            .allele());
        List<OptimizableValue<?>> optimizableValuesA = Collections.singletonList(optimizableValueA);
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        OptimizableValue<Integer> optimizableValueB = new OptimizableValue<>(optimizable1, b.genotype()
            .gene()
            .allele());
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        List<OptimizableValue<?>> optimizableValuesB = Collections.singletonList(optimizableValueB);
        when(normalizer.toOptimizableValues(b.genotype())).thenReturn(optimizableValuesB);
        Map<String, List<Double>> attributesA = new HashMap<>();
        attributesA.put("qa1", Arrays.asList(2.0));
        attributesA.put("qa2", Arrays.asList(2.0));
        Run runA = new Run(attributesA);
        Map<String, List<Double>> attributesB = new HashMap<>();
        attributesB.put("qa1", Arrays.asList(2.0));
        attributesB.put("qa2", Arrays.asList(2.0));
        Run runB = new Run(attributesB);
        QualityMeasurements qualityMeasurementsA = new QualityMeasurements(Collections.singletonList(runA));
        QualityMeasurements qualityMeasurementsB = new QualityMeasurements(Collections.singletonList(runB));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesA)).thenReturn(qualityMeasurementsA);
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesB)).thenReturn(qualityMeasurementsB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testComparePareto() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        OptimizableValue<Integer> optimizableValueA = new OptimizableValue<>(optimizable1, a.genotype()
            .gene()
            .allele());
        List<OptimizableValue<?>> optimizableValuesA = Collections.singletonList(optimizableValueA);
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        OptimizableValue<Integer> optimizableValueB = new OptimizableValue<>(optimizable1, b.genotype()
            .gene()
            .allele());
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        List<OptimizableValue<?>> optimizableValuesB = Collections.singletonList(optimizableValueB);
        when(normalizer.toOptimizableValues(b.genotype())).thenReturn(optimizableValuesB);
        Map<String, List<Double>> attributesA = new HashMap<>();
        attributesA.put("qa1", Arrays.asList(1.0));
        attributesA.put("qa2", Arrays.asList(2.0));
        Run runA = new Run(attributesA);
        Map<String, List<Double>> attributesB = new HashMap<>();
        attributesB.put("qa1", Arrays.asList(2.0));
        attributesB.put("qa2", Arrays.asList(1.0));
        Run runB = new Run(attributesB);
        QualityMeasurements qualityMeasurementsA = new QualityMeasurements(Collections.singletonList(runA));
        QualityMeasurements qualityMeasurementsB = new QualityMeasurements(Collections.singletonList(runB));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesA)).thenReturn(qualityMeasurementsA);
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesB)).thenReturn(qualityMeasurementsB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        OptimizableValue<Integer> optimizableValueA = new OptimizableValue<>(optimizable1, a.genotype()
            .gene()
            .allele());
        List<OptimizableValue<?>> optimizableValuesA = Collections.singletonList(optimizableValueA);
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        OptimizableValue<Integer> optimizableValueB = new OptimizableValue<>(optimizable1, b.genotype()
            .gene()
            .allele());
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        List<OptimizableValue<?>> optimizableValuesB = Collections.singletonList(optimizableValueB);
        when(normalizer.toOptimizableValues(b.genotype())).thenReturn(optimizableValuesB);
        Map<String, List<Double>> attributesA = new HashMap<>();
        attributesA.put("qa1", Arrays.asList(2.0));
        attributesA.put("qa2", Arrays.asList(2.0));
        Run runA = new Run(attributesA);
        Map<String, List<Double>> attributesB = new HashMap<>();
        attributesB.put("qa1", Arrays.asList(2.0));
        attributesB.put("qa2", Arrays.asList(3.0));
        Run runB = new Run(attributesB);
        QualityMeasurements qualityMeasurementsA = new QualityMeasurements(Collections.singletonList(runA));
        QualityMeasurements qualityMeasurementsB = new QualityMeasurements(Collections.singletonList(runB));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesA)).thenReturn(qualityMeasurementsA);
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesB)).thenReturn(qualityMeasurementsB);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNotDominating() {
        Phenotype<IntegerGene, Double> a = createPhenotype(1, 1.0);
        Phenotype<IntegerGene, Double> b = createPhenotype(2, 1.0);
        OptimizableValue<Integer> optimizableValueA = new OptimizableValue<>(optimizable1, a.genotype()
            .gene()
            .allele());
        List<OptimizableValue<?>> optimizableValuesA = Collections.singletonList(optimizableValueA);
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        OptimizableValue<Integer> optimizableValueB = new OptimizableValue<>(optimizable1, b.genotype()
            .gene()
            .allele());
        when(normalizer.toOptimizableValues(a.genotype())).thenReturn(optimizableValuesA);
        List<OptimizableValue<?>> optimizableValuesB = Collections.singletonList(optimizableValueB);
        when(normalizer.toOptimizableValues(b.genotype())).thenReturn(optimizableValuesB);
        Map<String, List<Double>> attributesA = new HashMap<>();
        attributesA.put("qa1", Arrays.asList(2.0));
        attributesA.put("qa2", Arrays.asList(3.0));
        Run runA = new Run(attributesA);
        Map<String, List<Double>> attributesB = new HashMap<>();
        attributesB.put("qa1", Arrays.asList(2.0));
        attributesB.put("qa2", Arrays.asList(2.0));
        Run runB = new Run(attributesB);
        QualityMeasurements qualityMeasurementsA = new QualityMeasurements(Collections.singletonList(runA));
        QualityMeasurements qualityMeasurementsB = new QualityMeasurements(Collections.singletonList(runB));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesA)).thenReturn(qualityMeasurementsA);
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValuesB)).thenReturn(qualityMeasurementsB);

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
