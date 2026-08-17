package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

/**
 * Samples the Optimizable values.
 * 
 * @author rapp
 *
 */
public class OptimizableValueProvider implements IFieldValueProvider {
    private final Map<Optimizable, Object> optimizableValues;

    public OptimizableValueProvider(List<OptimizableValue<?>> optimizableValues) {
        Comparator<Optimizable> comparator = Comparator.comparing(o -> o.getName());
        this.optimizableValues = new TreeMap<>(comparator);
        for (OptimizableValue<?> value : optimizableValues) {
            this.optimizableValues.put(value.getOptimizable(), value.getValue());
        }
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Object value = optimizableValues.get(field);
        return (Boolean) value;
    }

    @Override
    public Double getDoubleValue(Field field) {
        Object value = optimizableValues.get(field);
        return (Double) value;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        Object value = optimizableValues.get(field);
        return (Integer) value;
    }

    @Override
    public String getStringValue(Field field) {
        Object value = optimizableValues.get(field);
        return (String) value;
    }
}
