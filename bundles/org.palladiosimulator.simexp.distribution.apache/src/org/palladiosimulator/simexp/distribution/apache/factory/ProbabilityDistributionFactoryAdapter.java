package org.palladiosimulator.simexp.distribution.apache.factory;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.distribution.apache.function.NormalDistributionAdapter;
import org.palladiosimulator.simexp.distribution.apache.function.SimpleProbabilityMassFunction;
import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.distribution.function.ProbabilityDensityFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample;

public class ProbabilityDistributionFactoryAdapter<S> implements ProbabilityDistributionFactory<S> {

    @Override
    public ProbabilityMassFunction<S> pmfOver(Sample<S>... samples) {
        return pmfOver(new LinkedHashSet<>(Arrays.asList(samples)));
    }

    @Override
    public ProbabilityMassFunction<S> pmfOver(Set<Sample<S>> samples) {
        return new SimpleProbabilityMassFunction<>(samples);
    }

    @Override
    public ProbabilityMassFunction<S> uniformPmfOver(Set<S> values) {
        return new SimpleProbabilityMassFunction<>(asUniformSampleSpace(values));
    }

    @Override
    public ProbabilityDensityFunction normalDistributionWith(double mean, double variance) {
        return new NormalDistributionAdapter(mean, Math.sqrt(variance));
    }

    private Set<Sample<S>> asUniformSampleSpace(Set<S> values) {
        int spaceSize = values.size();
        return values.stream()
            .map(each -> ProbabilityMassFunction.Sample.of(each, (1 / spaceSize)))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
