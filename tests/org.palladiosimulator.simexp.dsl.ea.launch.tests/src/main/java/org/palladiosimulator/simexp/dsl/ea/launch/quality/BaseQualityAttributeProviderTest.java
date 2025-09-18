package org.palladiosimulator.simexp.dsl.ea.launch.quality;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class BaseQualityAttributeProviderTest {
    private BaseQualityAttributeProvider qualityAttributeProvider;

    private SmodelCreator smodelCreator;
    private Optimizable optimizable1;
    private OptimizableValue<Integer> optimizableValue1;

    @Before
    public void setUp() throws Exception {
        smodelCreator = new SmodelCreator();

        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable1 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);
        optimizableValue1 = new OptimizableValue<>(optimizable1, 1);

        qualityAttributeProvider = new BaseQualityAttributeProvider();
    }

    @Test
    public void testGetQualityMeasurementsSimple() {
        List<OptimizableValue<?>> optimizableValues1 = Collections.singletonList(optimizableValue1);
        QualityMeasurements measurements1 = new QualityMeasurements(Collections.emptyList());
        qualityAttributeProvider.put(optimizableValues1, measurements1);

        Optional<QualityMeasurements> actualQualityMeasurements = qualityAttributeProvider
            .getQualityMeasurements(optimizableValues1);

        assertThat(actualQualityMeasurements).get()
            .isEqualTo(measurements1);
    }

    @Test
    public void testGetQualityMeasurementsMissing() {
        OptimizableValue<Integer> optimizableValue2 = new OptimizableValue<>(optimizable1, 2);
        List<OptimizableValue<?>> optimizableValues2 = Collections.singletonList(optimizableValue2);

        Optional<QualityMeasurements> actualQualityMeasurements = qualityAttributeProvider
            .getQualityMeasurements(optimizableValues2);

        assertThat(actualQualityMeasurements).isNull();
    }

    @Test
    public void testGetQualityMeasurementsFailed() {
        OptimizableValue<Integer> optimizableValue2 = new OptimizableValue<>(optimizable1, 2);
        List<OptimizableValue<?>> optimizableValues2 = Collections.singletonList(optimizableValue2);
        qualityAttributeProvider.put(optimizableValues2, null);

        Optional<QualityMeasurements> actualQualityMeasurements = qualityAttributeProvider
            .getQualityMeasurements(optimizableValues2);

        assertThat(actualQualityMeasurements).isEmpty();
    }
}
