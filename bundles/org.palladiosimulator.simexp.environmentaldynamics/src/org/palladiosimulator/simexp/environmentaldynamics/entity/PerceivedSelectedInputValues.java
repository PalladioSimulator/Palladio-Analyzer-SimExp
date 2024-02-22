package org.palladiosimulator.simexp.environmentaldynamics.entity;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class PerceivedSelectedInputValues extends PerceivedInputValues
        implements PerceivedElement<List<InputValue<CategoricalValue>>> {

    private final Map<String, InputValue<CategoricalValue>> valueStore;

    public PerceivedSelectedInputValues(List<InputValue<CategoricalValue>> sample, Map<String, String> attributeMap) {
        super(sample);
        this.valueStore = buildValueStore(sample, attributeMap);
    }

    private Map<String, InputValue<CategoricalValue>> buildValueStore(List<InputValue<CategoricalValue>> sample,
            Map<String, String> attributeMap) {
        Map<String, InputValue<CategoricalValue>> store = new HashMap<>();
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            store.put(entry.getKey(), findInputValue(sample, entry.getValue()));
        }
        return store;
    }

    private InputValue<CategoricalValue> findInputValue(List<InputValue<CategoricalValue>> sample,
            String variableName) {
        Predicate<InputValue<CategoricalValue>> inputValue = inputValue(variableName);

        return sample.stream()
            .filter(inputValue)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                    String.format("Variable not found in sample | variableName: '%s' | sample: %s ", variableName,
                            StringUtils.join(sample, ","))));
    }

    private Predicate<InputValue<CategoricalValue>> inputValue(String variableName) {
        return inputValue -> {
            GroundRandomVariable groundRandomVariabe = inputValue.getVariable();
            String groundRandomVariableName = groundRandomVariabe.getEntityName();
            return groundRandomVariableName.equals(variableName);
        };
    }

    @Override
    public List<InputValue<CategoricalValue>> getValue() {
        return valueStore.values()
            .stream()
            .map(InputValue.class::cast)
            .collect(toList());
    }

    @Override
    public Optional<List<InputValue<CategoricalValue>>> getElement(String key) {
        InputValue<CategoricalValue> value = valueStore.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Collections.singletonList(value));
    }
}
