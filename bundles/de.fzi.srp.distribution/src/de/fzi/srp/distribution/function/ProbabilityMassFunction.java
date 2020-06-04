package de.fzi.srp.distribution.function;

import de.fzi.srp.distribution.function.ProbabilityMassFunction.Sample;

public interface ProbabilityMassFunction extends ProbabilityDistributionFunction<Sample> {

	public static class Sample {
		
		private Object value;
		private double probability;
		
		private Sample(Object value, double probability) {
			this.value = value;
			this.probability = probability;
		}
		
		public static Sample of(Object value) {
			return new Sample(value, 0);
		}
		
		public static Sample of(Object value, double probability) {
			return new Sample(value, probability);
		}

		public Object getValue() {
			return value;
		}

		public double getProbability() {
			return probability;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Sample) {
				return ((Sample) other).getValue().equals(value);
			}
			return false;
		}
	}
	
	public double probability(ProbabilityMassFunction.Sample sample);
}
