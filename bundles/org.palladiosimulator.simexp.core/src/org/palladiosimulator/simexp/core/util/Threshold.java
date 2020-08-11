package org.palladiosimulator.simexp.core.util;

import java.util.function.Predicate;

public class Threshold {
	private final double threshold;
	private final Predicate<Double> condition;
	
	private Threshold(double threshold, Predicate<Double> condition) {
		this.threshold = threshold;
		this.condition = condition;
	}
	
	public static Threshold lessThan(double threshold) {
		return new Threshold(threshold, value -> value < threshold);		
	}
	
	public static Threshold lessThanOrEqualTo(double threshold) {
		return new Threshold(threshold, value -> value <= threshold);		
	}
	
	public static Threshold greaterThan(double threshold) {
		return new Threshold(threshold, value -> value > threshold);		
	}
	
	public static Threshold greaterThanOrEqualTo(double threshold) {
		return new Threshold(threshold, value -> value >= threshold);		
	}
	
	public boolean isSatisfied(double value) {
		return condition.test(value);
	}
	
	public boolean isNotSatisfied(double value) {
		return isSatisfied(value) == false;
	}
	
	public double getValue() {
		return threshold;
	}
}
