package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;

public class PerceivedInputValue implements PerceivedValue<List<InputValue>> {

    private final List<InputValue> sample;

    public PerceivedInputValue(List<InputValue> sample) {
        this.sample = sample;
    }

    @Override
    public List<InputValue> getValue() {
        return sample;
    }

    @Override
    public Optional<Object> getElement(String key) {
        return Optional.of(sample);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (InputValue each : sample) {
            builder.append(String.format("(Variable: %1s, Value: %2s),", each.variable.getEntityName(),
                    each.value.toString()));
        }

        String stringValues = builder.toString();
        return String.format("Samples: [%s])", stringValues.substring(0, stringValues.length() - 1));
    }

}
