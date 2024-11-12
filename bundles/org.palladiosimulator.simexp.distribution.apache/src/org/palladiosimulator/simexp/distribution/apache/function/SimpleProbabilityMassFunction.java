package org.palladiosimulator.simexp.distribution.apache.function;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;

public class SimpleProbabilityMassFunction<S> implements ProbabilityMassFunction<S> {

    private final static double DEFAULT_VALUE = 0;

    EnumeratedDistribution<ProbabilityMassFunction.Sample<S>> enumDistribution;
    private boolean initialized = false;

    public SimpleProbabilityMassFunction(Set<ProbabilityMassFunction.Sample<S>> samples) {
        this.enumDistribution = new EnumeratedDistribution<>(asPairs(samples));
    }

    private List<Pair<ProbabilityMassFunction.Sample<S>, Double>> asPairs(Set<Sample<S>> samples) {
        return samples.stream()
            .map(each -> Pair.create(each, each.getProbability()))
            .collect(Collectors.toList());
    }

    @Override
    public void init(int seed) {
        initialized = true;
        enumDistribution.reseedRandomGenerator(seed);
    }

    @Override
    public ProbabilityMassFunction.Sample<S> drawSample() {
        if (!initialized) {
            throw new RuntimeException("not initialized");
        }
        return enumDistribution.sample();
    }

    @Override
    public double probability(ProbabilityMassFunction.Sample<S> sample) {
        return findSample(sample).orElse(DEFAULT_VALUE);
    }

    private Optional<Double> findSample(Sample<S> sample) {
        return enumDistribution.getPmf()
            .stream()
            .filter(p -> p.getKey()
                .equals(sample))
            .map(p -> p.getValue())
            .findFirst();
    }
}
