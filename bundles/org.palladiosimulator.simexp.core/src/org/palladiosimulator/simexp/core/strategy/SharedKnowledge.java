package org.palladiosimulator.simexp.core.strategy;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

public class SharedKnowledge {
	
	private final Map<String, Object> knowledgeStore = Maps.newHashMap();
	
	public void store(String key, Object value) {
		knowledgeStore.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getValue(String key) {
		Optional<Object> result = Optional.ofNullable(knowledgeStore.get(key));
		if (result.isPresent()) {
			return result.map(v -> (T) v);
		}
		return Optional.empty();
	}
	
}
