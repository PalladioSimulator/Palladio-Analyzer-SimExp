package org.palladiosimulator.simexp.core.entity;

public abstract class SimulatedMeasurementSpecification {
	
	private final String id;
	private final String name;
	
	public SimulatedMeasurementSpecification(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		if (isNotValid(other)) {
			return false;
		}
		return ((SimulatedMeasurementSpecification) other).getName().equals(name);
	}
	
	private boolean isNotValid(Object other) {
		return other == null || !(other instanceof SimulatedMeasurementSpecification);
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
