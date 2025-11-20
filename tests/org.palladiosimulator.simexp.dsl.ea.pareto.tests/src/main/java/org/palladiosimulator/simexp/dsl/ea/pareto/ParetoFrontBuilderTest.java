package org.palladiosimulator.simexp.dsl.ea.pareto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualParetoResult;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

public class ParetoFrontBuilderTest {
    private static final double EPSILON = 0.0001;

    private ParetoFrontBuilder builder;

    @Mock
    private IQualityAttributeProvider qualityAttributeProvider;
    @Mock
    private IPrecisionProvider precisionProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(precisionProvider.getPrecision()).thenReturn(EPSILON);

        builder = new ParetoFrontBuilder(qualityAttributeProvider, precisionProvider);
    }

    @Test
    public void testBuildParetoFront() {
        IndividualResult result = new IndividualResult(1.0, Collections.emptyList());
        List<IndividualResult> population = Collections.singletonList(result);

        List<IndividualParetoResult> actualFront = builder.buildParetoFront(population);

        assertThat(actualFront).hasSize(1);
    }

}
