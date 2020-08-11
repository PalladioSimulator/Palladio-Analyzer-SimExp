package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Optional;

public interface PerceivedValue<T> {
	
	public T getValue();
	
	public Optional<Object> getElement(String key);
	
}
