package org.palladiosimulator.simexp.dsl.ea.pareto.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class ParetoDominanceTest {
    static final double EPSILON = 0.0000001;

    private ParetoDominance paretoDominance;

    @Mock
    private IAverageProvider averageProvider;
    @Mock
    private IPrecisionProvider precisionProvider;

    private List<OptimizableValue<?>> optimizableValuesA;
    private List<OptimizableValue<?>> optimizableValuesB;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(precisionProvider.getPrecision()).thenReturn(EPSILON);

        SmodelCreator smodelCreator = new SmodelCreator();
        Optimizable optimizable = smodelCreator.createOptimizable("o", DataType.STRING, null);
        optimizableValuesA = Collections.singletonList(new OptimizableValue<>(optimizable, "a"));
        optimizableValuesB = Collections.singletonList(new OptimizableValue<>(optimizable, "b"));

        paretoDominance = new ParetoDominance(precisionProvider, averageProvider, s -> Double::compare);
    }

    @Test
    public void testCompareEqual() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testComparePrecision() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2.00000001, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2.00000002, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testComparePareto() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(1, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 1));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareDominating() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 3));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    @Test
    public void testCompareNotDominating() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 3));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareMissingBoth() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(Optional.empty());
        when(averageProvider.getAverages(b)).thenReturn(Optional.empty());

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareMissingA() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(Optional.empty());
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 2));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    @Test
    public void testCompareMissingB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 2));
        when(averageProvider.getAverages(b)).thenReturn(Optional.empty());

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNaNA() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", Double.NaN);
        averagesA.put("qa2", Double.NaN);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averagesA));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 3));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    @Test
    public void testCompareNaNB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", Double.NaN);
        averagesB.put("qa2", Double.NaN);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 3));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averagesB));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNaNAB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", Double.NaN);
        averages.put("qa2", Double.NaN);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averages));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averages));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareNegativeInfinityA() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", Double.NEGATIVE_INFINITY);
        averagesA.put("qa2", Double.NEGATIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averagesA));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 3));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    @Test
    public void testComparePositiveInfinityA() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", Double.POSITIVE_INFINITY);
        averagesA.put("qa2", Double.POSITIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averagesA));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(2, 3));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNegativeInfinityB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", Double.NEGATIVE_INFINITY);
        averagesB.put("qa2", Double.NEGATIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 3));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averagesB));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testComparePositiveInfinityB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", Double.POSITIVE_INFINITY);
        averagesB.put("qa2", Double.POSITIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(2, 3));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averagesB));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    @Test
    public void testComparePositiveInfinityANegativeInfinityB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", Double.POSITIVE_INFINITY);
        averagesA.put("qa2", Double.POSITIVE_INFINITY);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", Double.NEGATIVE_INFINITY);
        averagesB.put("qa2", Double.NEGATIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averagesA));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averagesB));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isGreaterThan(0);
    }

    @Test
    public void testCompareNegativeInfinityAPositiveInfinityB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averagesA = new HashMap<>();
        averagesA.put("qa1", Double.NEGATIVE_INFINITY);
        averagesA.put("qa2", Double.NEGATIVE_INFINITY);
        Map<String, Double> averagesB = new HashMap<>();
        averagesB.put("qa1", Double.POSITIVE_INFINITY);
        averagesB.put("qa2", Double.POSITIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averagesA));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averagesB));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isLessThan(0);
    }

    @Test
    public void testComparePositiveInfinityAB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", Double.POSITIVE_INFINITY);
        averages.put("qa2", Double.POSITIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averages));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averages));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareNegativeInfinityAB() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", Double.NEGATIVE_INFINITY);
        averages.put("qa2", Double.NEGATIVE_INFINITY);
        when(averageProvider.getAverages(a)).thenReturn(Optional.of(averages));
        when(averageProvider.getAverages(b)).thenReturn(Optional.of(averages));

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    @Test
    public void testCompareRegression_1c_gen21() {
        IndividualResult a = createIndividualResult(1.0, optimizableValuesA);
        IndividualResult b = createIndividualResult(1.0, optimizableValuesB);
        when(averageProvider.getAverages(a)).thenReturn(buildAverages(9.329275267205833, 0.07460441924724677));
        when(averageProvider.getAverages(b)).thenReturn(buildAverages(9.331774274217292, 0.07460431700913854));
        // when(precisionProvider.getPrecision()).thenReturn(EPSILON);
        // paretoDominance = new ParetoDominance(precisionProvider, averageProvider, s ->
        // Double::compare);

        int actualCompare = paretoDominance.compare(a, b);

        assertThat(actualCompare).isEqualTo(0);
    }

    private Optional<Map<String, Double>> buildAverages(double one, double two) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("qa1", one);
        averages.put("qa2", two);
        return Optional.of(averages);
    }

    private IndividualResult createIndividualResult(double fitness, List<OptimizableValue<?>> optimizableValues) {
        IndividualResult result = new IndividualResult(fitness, optimizableValues, "");
        return result;
    }
}
