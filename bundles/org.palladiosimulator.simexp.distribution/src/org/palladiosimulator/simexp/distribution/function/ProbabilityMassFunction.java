package org.palladiosimulator.simexp.distribution.function;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample;

public interface ProbabilityMassFunction<S> extends ProbabilityDistributionFunction<Sample<S>> {

    public static class Sample<S> {

        private final S value;
        private final double probability;

        private Sample(S value, double probability) {
            this.value = value;
            this.probability = probability;
        }

        public static <T> Sample<T> of(T value) {
            return new Sample<>(value, 0);
        }

        public static <T> Sample<T> of(T value, double probability) {
            return new Sample<>(value, probability);
        }

        public S getValue() {
            return value;
        }

        public double getProbability() {
            return probability;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Sample<S> rhs = (Sample<S>) obj;
            return new EqualsBuilder() //
                .append(value, rhs.value)
                .isEquals();
        }
    }

    public double probability(Sample<S> sample);
}
