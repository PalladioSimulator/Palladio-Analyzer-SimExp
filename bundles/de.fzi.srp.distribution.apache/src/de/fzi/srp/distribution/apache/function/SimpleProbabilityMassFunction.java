package de.fzi.srp.distribution.apache.function;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public class SimpleProbabilityMassFunction implements ProbabilityMassFunction {

	private final static double DEFAULT_VALUE = 0;
	
	EnumeratedDistribution<ProbabilityMassFunction.Sample> enumDistribution;
	
	public SimpleProbabilityMassFunction(Set<ProbabilityMassFunction.Sample> samples) {
		this.enumDistribution = new EnumeratedDistribution<ProbabilityMassFunction.Sample>(asPairs(samples));
	}
	
	private List<Pair<ProbabilityMassFunction.Sample,Double>> asPairs(Set<Sample> samples) {
		return samples.stream().map(each -> Pair.create(each, each.getProbability())).collect(Collectors.toList());
	}

	@Override
	public ProbabilityMassFunction.Sample drawSample() {
		return enumDistribution.sample();
	}

	@Override
	public double probability(ProbabilityMassFunction.Sample sample) {
		return findSample(sample).orElse(DEFAULT_VALUE);
	}
	
	private Optional<Double> findSample(Sample sample) {
		return enumDistribution.getPmf().stream().filter(p -> p.getKey().equals(sample))
												 .map(p -> p.getValue())	
												 .findFirst();
	}
}
