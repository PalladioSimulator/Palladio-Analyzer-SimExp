package org.palladiosimulator.simexp.distribution.factory;

import java.util.Set;

import org.palladiosimulator.simexp.distribution.function.ProbabilityDensityFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction.Sample;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

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
