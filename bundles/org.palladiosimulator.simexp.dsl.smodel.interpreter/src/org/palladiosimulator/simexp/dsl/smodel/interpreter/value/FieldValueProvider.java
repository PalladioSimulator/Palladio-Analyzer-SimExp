package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;

public class FieldValueProvider implements IFieldValueProvider {
    private final IFieldValueProvider constantValueProvider;
    private final IFieldValueProvider variableValueProvider;
    private final IFieldValueProvider probeValueProvider;
    private final IFieldValueProvider optimizableValueProvider;

    public FieldValueProvider(IFieldValueProvider constantValueProvider, IFieldValueProvider variableValueProvider,
            IFieldValueProvider probeValueProvider, IFieldValueProvider optimizableValueProvider) {
        this.constantValueProvider = constantValueProvider;
        this.variableValueProvider = variableValueProvider;
        this.probeValueProvider = probeValueProvider;
        this.optimizableValueProvider = optimizableValueProvider;
    }

    @Override
    public Boolean getBoolValue(Field field) {
        if (field instanceof Constant) {
            return constantValueProvider.getBoolValue(field);
        }
        if (field instanceof Variable) {
            return variableValueProvider.getBoolValue(field);
        }
        if (field instanceof Probe) {
            return probeValueProvider.getBoolValue(field);
        }
        if (field instanceof Optimizable) {
            return optimizableValueProvider.getBoolValue(field);
        }
        throw new RuntimeException("unknown field type: " + field);
    }

    @Override
    public Double getDoubleValue(Field field) {
        if (field instanceof Constant) {
            return constantValueProvider.getDoubleValue(field);
        }
        if (field instanceof Variable) {
            return variableValueProvider.getDoubleValue(field);
        }
        if (field instanceof Probe) {
            return probeValueProvider.getDoubleValue(field);
        }
        if (field instanceof Optimizable) {
            return optimizableValueProvider.getDoubleValue(field);
        }
        throw new RuntimeException("unknown field type: " + field);
    }

    @Override
    public Integer getIntegerValue(Field field) {
        if (field instanceof Constant) {
            return constantValueProvider.getIntegerValue(field);
        }
        if (field instanceof Variable) {
            return variableValueProvider.getIntegerValue(field);
        }
        if (field instanceof Probe) {
            return probeValueProvider.getIntegerValue(field);
        }
        if (field instanceof Optimizable) {
            return optimizableValueProvider.getIntegerValue(field);
        }
        throw new RuntimeException("unknown field type: " + field);
    }

    @Override
    public String getStringValue(Field field) {
        if (field instanceof Constant) {
            return constantValueProvider.getStringValue(field);
        }
        if (field instanceof Variable) {
            return variableValueProvider.getStringValue(field);
        }
        if (field instanceof Probe) {
            return probeValueProvider.getStringValue(field);
        }
        if (field instanceof Optimizable) {
            return optimizableValueProvider.getStringValue(field);
        }
        throw new RuntimeException("unknown field type: " + field);
    }
}
