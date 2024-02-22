package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;

import com.google.common.collect.Lists;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class PerceivedInputValues implements PerceivedValue<List<InputValue<CategoricalValue>>> {

    private final List<InputValue<CategoricalValue>> samples;

    public PerceivedInputValues(List<InputValue<CategoricalValue>> samples) {
        this.samples = samples;
    }

    @Override
    public List<InputValue<CategoricalValue>> getValue() {
        return samples;
    }

    private List<InputValue<CategoricalValue>> getSortedSamples() {
        List<InputValue<CategoricalValue>> orderedSamples = Lists.newArrayList(samples);
        Collections.sort(orderedSamples, new Comparator<>() {
            @Override
            public int compare(InputValue<CategoricalValue> i1, InputValue<CategoricalValue> i2) {
                return i1.getVariable()
                    .getEntityName()
                    .compareTo(i2.getVariable()
                        .getEntityName());
            }
        });
        return orderedSamples;
    }

    @Override
    public String toString() {
        List<String> entries = new ArrayList<>();
        for (InputValue<CategoricalValue> each : getSortedSamples()) {
            entries.add(String.format("(Variable: %1s, Value: %2s)", each.getVariable()
                .getEntityName(),
                    each.getValue()
                        .toString()));
        }
        return String.format("Samples: [%s])", StringUtils.join(entries, ","));
    }
}
