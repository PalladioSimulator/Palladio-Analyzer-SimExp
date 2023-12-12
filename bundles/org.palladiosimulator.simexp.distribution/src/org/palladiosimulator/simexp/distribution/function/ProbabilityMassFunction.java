package org.palladiosimulator.simexp.distribution.function;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample;

public interface ProbabilityMassFunction<T> extends ProbabilityDistributionFunction<Sample<T>> {

    public static class Sample<T> {

        private T value;
        private double probability;

        private Sample(T value, double probability) {
            this.value = value;
            this.probability = probability;
        }

        public static <T> Sample<T> of(T value) {
            return new Sample<>(value, 0);
        }

        public static <T> Sample<T> of(T value, double probability) {
            return new Sample<>(value, probability);
        }

        public T getValue() {
            return value;
        }

        public double getProbability() {
            return probability;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Sample) {
                return ((Sample<?>) other).getValue()
                    .equals(value);
            }
            return false;
        }
    }

    public double probability(ProbabilityMassFunction.Sample<T> sample);
}
