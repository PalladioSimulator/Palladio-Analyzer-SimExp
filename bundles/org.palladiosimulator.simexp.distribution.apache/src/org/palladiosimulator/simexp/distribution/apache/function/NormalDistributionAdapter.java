package org.palladiosimulator.simexp.distribution.apache.function;

import java.util.Optional;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.palladiosimulator.simexp.distribution.function.ProbabilityDensityFunction;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class NormalDistributionAdapter implements ProbabilityDensityFunction {

    private final NormalDistribution normalDistribution;
    private boolean initialized = false;

    public NormalDistributionAdapter(double mean, double sd) {
        this.normalDistribution = new NormalDistribution(mean, sd);
    }

    @Override
    public void init(Optional<ISeedProvider> seedProvider) {
        initialized = true;
        seedProvider.ifPresent(sp -> normalDistribution.reseedRandomGenerator(sp.getLong()));
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
