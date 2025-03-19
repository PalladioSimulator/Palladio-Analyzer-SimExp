package org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class StaticValueProviderTest {
    private StaticValueProvider provider;

    private SmodelCreator smodelCreator;

    @Mock
    private IFieldValueProvider dynamicValueProvider;

    @Before
    public void setUp() {
        initMocks(this);

        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testGetIntegerValue() {
        EnvVariable intVariable = smodelCreator.createEnvVariable("ev", DataType.INT, "id");
        StaticValueProvider provider = new StaticValueProvider(dynamicValueProvider,
                Collections.singletonList(intVariable));
        when(dynamicValueProvider.getIntegerValue(intVariable)).thenReturn(0);
        provider.assignStatic();
        when(dynamicValueProvider.getIntegerValue(intVariable)).thenReturn(1);

        Integer actualValue = provider.getIntegerValue(intVariable);

        assertThat(actualValue).isEqualTo(0);
    }
}
