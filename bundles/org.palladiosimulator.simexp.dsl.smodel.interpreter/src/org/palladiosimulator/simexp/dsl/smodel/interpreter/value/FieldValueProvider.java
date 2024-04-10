package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public class FieldValueProvider implements IFieldValueProvider {
    private final IFieldValueProvider probeValueProvider;
    private final IFieldValueProvider optimizableValueProvider;
    private final IFieldValueProvider constantValueProvider;

    public FieldValueProvider(IFieldValueProvider probeValueProvider, IFieldValueProvider optimizableValueProvider) {
        this(probeValueProvider, optimizableValueProvider, new ConstantValueProvider());
    }

    public FieldValueProvider(IFieldValueProvider probeValueProvider, IFieldValueProvider optimizableValueProvider,
            IFieldValueProvider constantValueProvider) {
        this.probeValueProvider = probeValueProvider;
        this.optimizableValueProvider = optimizableValueProvider;
        this.constantValueProvider = constantValueProvider;
    }

    @Override
    public Boolean getBoolValue(Field field) {
        if (field instanceof Constant) {
            return constantValueProvider.getBoolValue(field);
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
        if (field instanceof Probe) {
            return probeValueProvider.getStringValue(field);
        }
        if (field instanceof Optimizable) {
            return optimizableValueProvider.getStringValue(field);
        }
        throw new RuntimeException("unknown field type: " + field);
    }
}
