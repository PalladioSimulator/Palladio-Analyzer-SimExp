package org.palladiosimulator.simexp.core.strategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
	
	public Collection<Object> getValues() {
		return knowledgeStore.values();
	}
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("SharedKnowledge[");
        Iterator<Entry<String, Object>> iter = knowledgeStore.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Object> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        sb.append("]");
        return sb.toString();
	}
	
}
