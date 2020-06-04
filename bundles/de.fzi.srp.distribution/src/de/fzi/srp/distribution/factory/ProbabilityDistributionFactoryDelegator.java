package de.fzi.srp.distribution.factory;

import java.util.Set;

import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

import de.fzi.srp.distribution.function.ProbabilityDensityFunction;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;
import de.fzi.srp.distribution.function.ProbabilityMassFunction.Sample;

public class ProbabilityDistributionFactoryDelegator implements ProbabilityDistributionFactory {

	private ProbabilityDistributionFactory delegatedFactory;

	public ProbabilityDistributionFactoryDelegator() {
		// TODO exception handling
		this.delegatedFactory = ServiceRegistry.get().findService(ProbabilityDistributionFactory.class)
				.orElseThrow(() -> new RuntimeException(""));
	}

	@Override
	public ProbabilityMassFunction pmfOver(Sample... samples) {
		return delegatedFactory.pmfOver(samples);
	}

	@Override
	public ProbabilityMassFunction pmfOver(Set<ProbabilityMassFunction.Sample> samples) {
		return delegatedFactory.pmfOver(samples);
	}

	@Override
	public ProbabilityMassFunction uniformPmfOver(Set<Object> values) {
		return delegatedFactory.uniformPmfOver(values);
	}

	@Override
	public ProbabilityDensityFunction normalDistributionWith(double mean, double variance) {
		return delegatedFactory.normalDistributionWith(mean, variance);
	}

}
