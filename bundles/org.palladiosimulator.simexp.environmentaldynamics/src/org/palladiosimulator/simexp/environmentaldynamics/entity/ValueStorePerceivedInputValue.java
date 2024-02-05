package org.palladiosimulator.simexp.environmentaldynamics.entity;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;

import com.google.common.collect.Lists;

public class ValueStorePerceivedInputValue implements PerceivedElement<List<InputValue>> {

    private final List<InputValue> sample;
    private final Map<String, InputValue> valueStore;

    public ValueStorePerceivedInputValue(List<InputValue> sample, Map<String, InputValue> valueStore) {
        this.sample = sample;
        this.valueStore = valueStore;
    }

    @Override
    public List<InputValue> getValue() {
        return valueStore.values()
            .stream()
            .map(InputValue.class::cast)
            .collect(toList());
    }

    @Override
    public Optional<List<InputValue>> getElement(String key) {
        InputValue value = valueStore.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Collections.singletonList(value));
    }

    private List<InputValue> getSamples() {
        List<InputValue> orderedSamples = Lists.newArrayList(sample);
        Collections.sort(orderedSamples, new Comparator<>() {
            @Override
            public int compare(InputValue i1, InputValue i2) {
                return i1.variable.getEntityName()
                    .compareTo(i2.variable.getEntityName());
            }
        });
        return orderedSamples;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (InputValue each : getSamples()) {
            builder.append(String.format("(Variable: %1s, Value: %2s),", each.variable.getEntityName(),
                    each.value.toString()));
        }

        String stringValues = builder.toString();
        return String.format("Samples: [%s])", stringValues.substring(0, stringValues.length() - 1));
    }
}
