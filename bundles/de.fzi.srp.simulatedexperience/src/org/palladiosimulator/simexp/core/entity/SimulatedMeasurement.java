package org.palladiosimulator.simexp.core.entity;

public class SimulatedMeasurement {

	protected final static String VALUE_TAG = "value";
	protected final static String SPEC_TAG = "spec";
	
	private final SimulatedMeasurementSpecification specification;
	
	private double value;
	
	private SimulatedMeasurement(double value, SimulatedMeasurementSpecification specification) {
		this.value = value;
		this.specification = specification;
	}
	
	public static SimulatedMeasurement with(SimulatedMeasurementSpecification specification) {
		return new SimulatedMeasurement(Double.POSITIVE_INFINITY, specification);
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public SimulatedMeasurementSpecification getSpecification() {
		return specification;
	}
	
	@Override
	public String toString() {
		return String.format("{%1s: %2s, %3s: %4s}", VALUE_TAG, getValue(), SPEC_TAG, getSpecification());
	}
	
}
