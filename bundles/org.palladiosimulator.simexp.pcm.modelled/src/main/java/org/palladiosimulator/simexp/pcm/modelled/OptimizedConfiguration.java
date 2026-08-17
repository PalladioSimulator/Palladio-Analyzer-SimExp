package org.palladiosimulator.simexp.pcm.modelled;

import java.util.Map;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.modelled.config.impl.SimpleOptimizedConfiguration;

public class OptimizedConfiguration extends SimpleOptimizedConfiguration {

    private final Map<String, Object> optimizedValues;

    public OptimizedConfiguration(Smodel smodel, Map<String, Object> optimizedValues) {
        super(smodel);
        this.optimizedValues = optimizedValues;
    }

    @Override
    protected OptimizableValue<Boolean> getOptimizableValueBool(Optimizable optimizable) {
        Object value = optimizedValues.get(optimizable.getName());
        if (value == null) {
            throw new RuntimeException(String.format("value missing for bool optimizable: %s", optimizable.getName()));
        }
        return new OptimizableValue<>(optimizable, (Boolean) value);
    }

    @Override
    protected OptimizableValue<String> getOptimizableValueString(Optimizable optimizable) {
        Object value = optimizedValues.get(optimizable.getName());
        if (value == null) {
            throw new RuntimeException(
                    String.format("value missing for string optimizable: %s", optimizable.getName()));
        }
        return new OptimizableValue<>(optimizable, (String) value);
    }

    @Override
    protected OptimizableValue<Integer> getOptimizableValueInt(Optimizable optimizable) {
        Object value = optimizedValues.get(optimizable.getName());
        if (value == null) {
            throw new RuntimeException(String.format("value missing for int optimizable: %s", optimizable.getName()));
        }
        return new OptimizableValue<>(optimizable, (Integer) value);
    }

    @Override
    protected OptimizableValue<Double> getOptimizableValueDouble(Optimizable optimizable) {
        Object value = optimizedValues.get(optimizable.getName());
        if (value == null) {
            throw new RuntimeException(
                    String.format("value missing for double optimizable: %s", optimizable.getName()));
        }
        return new OptimizableValue<>(optimizable, (Double) value);
    }
}
