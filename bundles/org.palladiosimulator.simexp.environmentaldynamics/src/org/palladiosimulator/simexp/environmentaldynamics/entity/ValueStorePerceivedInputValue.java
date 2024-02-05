package org.palladiosimulator.simexp.environmentaldynamics.entity;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;

import com.google.common.collect.Lists;

public class ValueStorePerceivedInputValue extends PerceivedInputValue implements PerceivedElement<List<InputValue>> {

    private final Map<String, InputValue> valueStore;

    public ValueStorePerceivedInputValue(List<InputValue> sample, Map<String, String> attributeMap) {
        super(sample);
        this.valueStore = buildValueStore(sample, attributeMap);
    }

    private Map<String, InputValue> buildValueStore(List<InputValue> sample, Map<String, String> attributeMap) {
        Map<String, InputValue> store = new HashMap<>();
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            store.put(entry.getKey(), findInputValue(sample, entry.getValue()));
        }
        return store;
    }

    private InputValue findInputValue(List<InputValue> sample, String variableName) {
        Predicate<InputValue> inputValue = inputValue(variableName);

        return sample.stream()
            .filter(inputValue)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                    String.format("Variable not found in sample | variableName: '%s' | sample: %s ", variableName,
                            StringUtils.join(sample, ","))));
    }

    private Predicate<InputValue> inputValue(String variableName) {
        return inputValue -> {
            GroundRandomVariable groundRandomVariabe = inputValue.variable;
            String groundRandomVariableName = groundRandomVariabe.getEntityName();
            return groundRandomVariableName.equals(variableName);
        };
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
        List<InputValue> orderedSamples = Lists.newArrayList(super.getValue());
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
