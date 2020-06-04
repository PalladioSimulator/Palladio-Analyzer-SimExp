package de.fzi.srp.distribution.apache.factory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.fzi.srp.distribution.apache.function.NormalDistributionAdapter;
import de.fzi.srp.distribution.apache.function.SimpleProbabilityMassFunction;
import de.fzi.srp.distribution.factory.ProbabilityDistributionFactory;
import de.fzi.srp.distribution.function.ProbabilityDensityFunction;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;
import de.fzi.srp.distribution.function.ProbabilityMassFunction.Sample;

public class ProbabilityDistributionFactoryAdapter implements ProbabilityDistributionFactory {
	
	@Override
	public ProbabilityMassFunction pmfOver(Sample... samples) {
		return pmfOver(new HashSet<Sample>(Arrays.asList(samples)));
	}

	@Override
	public ProbabilityMassFunction pmfOver(Set<Sample> samples) {
		return new SimpleProbabilityMassFunction(samples);
	}

	@Override
	public ProbabilityMassFunction uniformPmfOver(Set<Object> values) {
		return new SimpleProbabilityMassFunction(asUniformSampleSpace(values));
	}

	@Override
	public ProbabilityDensityFunction normalDistributionWith(double mean, double variance) {
		return new NormalDistributionAdapter(mean, Math.sqrt(variance));
	}

	private Set<Sample> asUniformSampleSpace(Set<Object> values) {
		int spaceSize = values.size();
		return values.stream().map(each -> ProbabilityMassFunction.Sample.of(each, (1 / spaceSize)))
							  .collect(Collectors.toSet());
	}
	
}
