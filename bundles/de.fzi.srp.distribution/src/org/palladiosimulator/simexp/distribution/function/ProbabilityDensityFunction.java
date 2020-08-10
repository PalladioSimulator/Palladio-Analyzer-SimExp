package org.palladiosimulator.simexp.distribution.function;

public interface ProbabilityDensityFunction extends ProbabilityDistributionFunction<Double> {

	public double density(double value);
}
