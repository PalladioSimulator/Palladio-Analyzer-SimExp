package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class QualityAttributesAverageCalculatorTest {
    private QualityAttributesAverageCalculator calculator;

    @Mock
    private IQualityAttributeProvider qualityAttributeProvider;

    private SmodelCreator smodelCreator;
    private Optimizable optimizable1;
    private OptimizableValue<Integer> optimizableValue1;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        smodelCreator = new SmodelCreator();

        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable1 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);
        optimizableValue1 = new OptimizableValue<>(optimizable1, 1);

        calculator = new QualityAttributesAverageCalculator(qualityAttributeProvider);
    }

    @Test
    public void testSingleAttribute() {
        List<OptimizableValue<?>> optimizableValues1 = Collections.singletonList(optimizableValue1);
        Run run1 = new Run(Collections.singletonMap("qa1", Arrays.asList(1.0, 2.0)));
        QualityMeasurements qualityMeasurements1 = new QualityMeasurements(Arrays.asList(run1));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValues1)).thenReturn(qualityMeasurements1);

        Map<String, Double> actualAverages = calculator.calculateAverages(optimizableValues1);

        assertThat(actualAverages).containsOnly(entry("qa1", 1.5));
    }

    @Test
    public void testDoubleAttribute() {
        List<OptimizableValue<?>> optimizableValues1 = Collections.singletonList(optimizableValue1);
        Map<String, List<Double>> qualityAttributes = new HashMap<>();
        qualityAttributes.put("qa1", Arrays.asList(1.0, 2.0));
        qualityAttributes.put("qa2", Arrays.asList(1.0, 1.0));
        Run run1 = new Run(qualityAttributes);
        QualityMeasurements qualityMeasurements1 = new QualityMeasurements(Arrays.asList(run1));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValues1)).thenReturn(qualityMeasurements1);

        Map<String, Double> actualAverages = calculator.calculateAverages(optimizableValues1);

        assertThat(actualAverages).containsOnly(entry("qa1", 1.5), entry("qa2", 1.0));
    }
}
