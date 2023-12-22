package org.palladiosimulator.simexp.distribution.factory;

import java.util.Set;

import org.palladiosimulator.simexp.distribution.function.ProbabilityDensityFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class ProbabilityDistributionFactoryDelegator<S> implements ProbabilityDistributionFactory<S> {

    private ProbabilityDistributionFactory<S> delegatedFactory;

    public ProbabilityDistributionFactoryDelegator() {
        // TODO exception handling
        this.delegatedFactory = ServiceRegistry.get()
            .findService(ProbabilityDistributionFactory.class)
            .orElseThrow(() -> new RuntimeException(""));
    }

    @Override
    public ProbabilityMassFunction<S> pmfOver(Sample<S>... samples) {
        return delegatedFactory.pmfOver(samples);
    }

    @Override
    public ProbabilityMassFunction<S> pmfOver(Set<ProbabilityMassFunction.Sample<S>> samples) {
        return delegatedFactory.pmfOver(samples);
    }

    @Override
    public ProbabilityMassFunction<S> uniformPmfOver(Set<S> values) {
        return delegatedFactory.uniformPmfOver(values);
    }

    @Override
    public ProbabilityDensityFunction normalDistributionWith(double mean, double variance) {
        return delegatedFactory.normalDistributionWith(mean, variance);
    }

}
