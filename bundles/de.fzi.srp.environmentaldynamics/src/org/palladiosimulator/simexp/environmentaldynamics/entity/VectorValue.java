package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Vector;

public class VectorValue implements PerceivedValue<Vector<Double>> {
	
	private final LinkedHashMap<String, Double> vectorElements;
	
	public VectorValue(LinkedHashMap<String, Double> vectorElements) {
		this.vectorElements = vectorElements;
	}

	@Override
	public Vector<Double> getValue() {
		return new Vector<>(vectorElements.values());
	}
	
	@Override
	public Optional<Object> getElement(String key) {
		return Optional.ofNullable(vectorElements.get(key));
	}
	
	public Optional<Double> getElement(int index) {
		if (index < 0 || index >= vectorElements.size()) {
			return Optional.empty();
		}
		return Optional.of(getElementAt(index));
	}
	
	public double getElementAt(int index) {
		return (double) vectorElements.values().toArray()[index];
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String each : vectorElements.keySet()) {
			builder.append(String.format("[%1s: %2s]\n", each, vectorElements.get(each)));
		}
		builder.deleteCharAt(builder.lastIndexOf("\n"));
		return builder.toString();
	}

}
