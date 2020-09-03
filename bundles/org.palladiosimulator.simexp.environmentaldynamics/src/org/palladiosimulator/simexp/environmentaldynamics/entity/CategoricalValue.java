package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Optional;

public class CategoricalValue implements PerceivedValue<String> {

	private final String name;
	private final String value;
	
	public CategoricalValue(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String getValue() {
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
		return String.format("%1s: %2s", name, value);
	}

}
