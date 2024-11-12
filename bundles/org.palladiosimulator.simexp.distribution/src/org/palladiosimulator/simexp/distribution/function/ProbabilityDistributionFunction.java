package org.palladiosimulator.simexp.distribution.function;

public interface ProbabilityDistributionFunction<T> {
    void init(int seed);

    public T drawSample();

}
