package org.palladiosimulator.simexp.dsl.ea.api.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class OptimizableValueToString {
    public String asString(List<OptimizableValue<?>> optimizableValues) {
        List<OptimizableValue<?>> sortedOptimizableValues = new ArrayList<>(optimizableValues);
        sortedOptimizableValues.sort(Comparator.comparing(o -> o.getOptimizable()
            .getName()));

        List<String> entries = new ArrayList<>();
        for (OptimizableValue<?> ov : sortedOptimizableValues) {
            entries.add(String.format("%s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        return StringUtils.join(entries, ",");
    }
}
