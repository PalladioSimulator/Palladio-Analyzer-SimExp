package org.palladiosimulator.simexp.distribution.apache.function;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.palladiosimulator.simexp.distribution.function.ProbabilityDensityFunction;

public class NormalDistributionAdapter implements ProbabilityDensityFunction {

    private final NormalDistribution normalDistribution;
    private boolean initialized = false;

    public NormalDistributionAdapter(double mean, double sd) {
        this.normalDistribution = new NormalDistribution(mean, sd);
    }

    @Override
    public void init(int seed) {
        initialized = true;
        normalDistribution.reseedRandomGenerator(seed);
    }

    @Override
    public Double drawSample() {
        if (!initialized) {
            throw new RuntimeException("not initialized");
        }
        return normalDistribution.sample();
    }

    @Override
    public double density(double value) {
        return normalDistribution.density(value);
    }

}
