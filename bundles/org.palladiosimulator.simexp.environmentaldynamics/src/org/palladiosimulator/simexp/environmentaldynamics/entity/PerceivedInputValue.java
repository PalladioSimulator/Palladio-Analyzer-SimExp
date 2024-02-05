package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;

import com.google.common.collect.Lists;

public class PerceivedInputValue implements PerceivedValue<List<InputValue>> {

    private final List<InputValue> sample;

    public PerceivedInputValue(List<InputValue> sample) {
        this.sample = sample;
    }

    @Override
    public List<InputValue> getValue() {
        return sample;
    }

    private List<InputValue> getSortedSamples() {
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
        for (InputValue each : getSortedSamples()) {
            builder.append(String.format("(Variable: %1s, Value: %2s),", each.variable.getEntityName(),
                    each.value.toString()));
        }

        String stringValues = builder.toString();
        return String.format("Samples: [%s])", stringValues.substring(0, stringValues.length() - 1));
    }
}
