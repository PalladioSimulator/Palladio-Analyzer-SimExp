package org.palladiosimulator.simexp.core.statistics;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StatisticalQuantities {
	
	private final DescriptiveStatistics stats;
	
	private StatisticalQuantities(List<Double> values) {
		this.stats = new DescriptiveStatistics(asArray(values));
	}
	
	public static StatisticalQuantities withDoubles(List<Double> values) {
		return new StatisticalQuantities(values);
	}
	
	public static StatisticalQuantities withNumbers(List<Number> values) {
		return new StatisticalQuantities(values.stream().map(v -> (Double) v).collect(Collectors.toList()));
	}
	
	public double variance() {
		return stats.getVariance();
	}
	
	public double mean() {
		return stats.getMean();
	}
	
	public double sum() {
		return stats.getSum();
	}
	
	private double[] asArray(List<Double> values) {
		double[] copy = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			copy[i] = values.get(i);
		}
		return copy;
	}
}
