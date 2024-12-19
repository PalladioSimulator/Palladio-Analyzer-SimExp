package org.palladiosimulator.simexp.distribution.factory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.distribution.function.ProbabilityDensityFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;

public interface ProbabilityDistributionFactory<S> {

    public static <S> Set<Object> toValueSpace(Set<S> values) {
        return values.stream()
            .map(each -> (Object) each)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public final static ProbabilityDistributionFactory INSTANCE = new ProbabilityDistributionFactoryDelegator<>();

    public ProbabilityMassFunction<S> pmfOver(ProbabilityMassFunction.Sample<S>... samples);

    public ProbabilityMassFunction<S> pmfOver(Set<ProbabilityMassFunction.Sample<S>> samples);

    public ProbabilityMassFunction<S> uniformPmfOver(Set<S> values);

    public ProbabilityDensityFunction normalDistributionWith(double mean, double variance);
}
