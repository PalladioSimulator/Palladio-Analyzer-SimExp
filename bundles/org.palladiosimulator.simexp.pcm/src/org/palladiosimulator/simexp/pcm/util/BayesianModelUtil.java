package org.palladiosimulator.simexp.pcm.util;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

import com.google.common.collect.Lists;

public class BayesianModelUtil {

	public static PerceivedValue<?> asPerceivedValue(List<InputValue> sample, Map<String, InputValue> newValueStore) {
		return new PerceivedValue<List<InputValue>>() {

			private final Map<String, InputValue> valueStore = newValueStore;
			
			@Override
			public List<InputValue> getValue() {
				return valueStore.values().stream()
						.map(InputValue.class::cast)
						.collect(toList());
			}

			@Override
			public Optional<Object> getElement(String key) {
				return Optional.ofNullable(valueStore.get(key)).map(InputValue::asCategorical);
			}

			@Override
			public String toString() {
				List<InputValue> orderedSamples = Lists.newArrayList(sample);
				Collections.sort(orderedSamples, new Comparator<InputValue>() {
					@Override
					public int compare(InputValue i1, InputValue i2) {
						return i1.variable.getEntityName().compareTo(i2.variable.getEntityName());
					}
				});
				
				StringBuilder builder = new StringBuilder();
				for (InputValue each : orderedSamples) {
					builder.append(String.format("(Variable: %1s, Value: %2s),", each.variable.getEntityName(),
							each.value.toString()));
				}
				String stringValues = builder.toString();
				return String.format("Samples: [%s])", stringValues.substring(0, stringValues.length() - 1));
			}

		};
	}
	
}
