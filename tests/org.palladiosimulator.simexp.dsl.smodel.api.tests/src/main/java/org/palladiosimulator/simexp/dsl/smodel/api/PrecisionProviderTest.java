package org.palladiosimulator.simexp.dsl.smodel.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import org.junit.Before;
import org.junit.Test;

public class PrecisionProviderTest {
    private final static double EPSILON = 0.00001;

    private final static int PLACES = 3;

    private PrecisionProvider precisionProvider;

    @Before
    public void setUp() throws Exception {
        this.precisionProvider = new PrecisionProvider(PLACES);
    }

    @Test
    public void testGetPrecision() {
        double actualPrecision = precisionProvider.getPrecision();

        assertThat(actualPrecision).isEqualTo(0.0001, withPrecision(EPSILON));
    }
}
