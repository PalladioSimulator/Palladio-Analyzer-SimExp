package de.fzi.srp.distribution.factory;

import java.util.Set;
import java.util.stream.Collectors;

import de.fzi.srp.distribution.function.ProbabilityDensityFunction;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public interface ProbabilityDistributionFactory {

	public static <T> Set<Object> toValueSpace(Set<T> values) {
		return values.stream().map(each -> (Object) each).collect(Collectors.toSet());
	}
	
	public final static ProbabilityDistributionFactory INSTANCE = new ProbabilityDistributionFactoryDelegator();
	
	public ProbabilityMassFunction pmfOver(ProbabilityMassFunction.Sample...samples);
	
	public ProbabilityMassFunction pmfOver(Set<ProbabilityMassFunction.Sample> samples);
	
	public ProbabilityMassFunction uniformPmfOver(Set<Object> values);
	
	public ProbabilityDensityFunction normalDistributionWith(double mean, double variance);
}
