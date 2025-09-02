package org.palladiosimulator.simexp.distribution.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample;

public class ProbabilityMassFunctionTest {

    @Test
    public void testSampleEqualsValue() {
        Sample<Integer> s1 = Sample.of(1, 0.1);
        Sample<Integer> s2 = Sample.of(1, 0.2);

        boolean actualEquals = s1.equals(s2);

        assertThat(actualEquals).isTrue();
    }

    @Test
    public void testSampleNotEqualsValue() {
        Sample<Integer> s1 = Sample.of(2, 0.1);
        Sample<Integer> s2 = Sample.of(1, 0.1);

        boolean actualEquals = s1.equals(s2);

        assertThat(actualEquals).isFalse();
    }
}
