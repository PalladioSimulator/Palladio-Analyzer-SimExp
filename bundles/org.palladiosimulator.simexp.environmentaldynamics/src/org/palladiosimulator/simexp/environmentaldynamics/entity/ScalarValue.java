package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Optional;

public class ScalarValue implements PerceivedValue<Double> {

	private final String name;
	private final Double value;
	
	public ScalarValue(String name, Double value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public Optional<Object> getElement(String key) {
		if (key == name) {
			return Optional.of(value);
		}
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		return String.format("[%1s: %2s]", name, value);
	}

}
