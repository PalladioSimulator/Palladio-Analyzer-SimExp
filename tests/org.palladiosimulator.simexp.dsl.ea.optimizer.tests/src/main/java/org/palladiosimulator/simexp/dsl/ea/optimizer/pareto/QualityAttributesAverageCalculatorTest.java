package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private OptimizableValue<Integer> optimizableValue2;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        smodelCreator = new SmodelCreator();

        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable1 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);
        optimizableValue1 = new OptimizableValue<>(optimizable1, 1);
        optimizableValue2 = new OptimizableValue<>(optimizable1, 2);

        calculator = new QualityAttributesAverageCalculator(qualityAttributeProvider);
    }

    @Test
    public void testSingleAttribute() {
        List<OptimizableValue<?>> optimizableValues1 = Collections.singletonList(optimizableValue1);
        List<List<OptimizableValue<?>>> optimizableValuesList = new ArrayList<>();
        optimizableValuesList.add(optimizableValues1);
        Run run1 = new Run(Collections.singletonMap("qa1", Arrays.asList(1.0, 2.0)));
        QualityMeasurements qualityMeasurements1 = new QualityMeasurements(Arrays.asList(run1));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValues1)).thenReturn(qualityMeasurements1);

        Map<String, Double> actualAverages = calculator.calculateAverages(optimizableValuesList);

        assertThat(actualAverages).containsOnly(entry("qa1", 1.5));
    }

    @Test
    public void testDoubleAttribute() {
        List<List<OptimizableValue<?>>> optimizableValuesList = new ArrayList<>();
        List<OptimizableValue<?>> optimizableValues1 = Collections.singletonList(optimizableValue1);
        optimizableValuesList.add(optimizableValues1);
        Run run1 = new Run(Collections.singletonMap("qa1", Arrays.asList(1.0, 2.0)));
        QualityMeasurements qualityMeasurements1 = new QualityMeasurements(Arrays.asList(run1));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValues1)).thenReturn(qualityMeasurements1);
        List<OptimizableValue<?>> optimizableValues2 = Collections.singletonList(optimizableValue2);
        optimizableValuesList.add(optimizableValues2);
        Run run2 = new Run(Collections.singletonMap("qa2", Arrays.asList(1.0, 1.0)));
        QualityMeasurements qualityMeasurements2 = new QualityMeasurements(Arrays.asList(run2));
        when(qualityAttributeProvider.getQualityMeasurements(optimizableValues2)).thenReturn(qualityMeasurements2);

        Map<String, Double> actualAverages = calculator.calculateAverages(optimizableValuesList);

        assertThat(actualAverages).containsOnly(entry("qa1", 1.5), entry("qa2", 1.0));
    }
}
