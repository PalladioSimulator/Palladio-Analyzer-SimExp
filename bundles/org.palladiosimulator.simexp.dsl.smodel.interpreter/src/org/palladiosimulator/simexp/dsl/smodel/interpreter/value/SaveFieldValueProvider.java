package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public class SaveFieldValueProvider implements IFieldValueProvider {
    private final IFieldValueProvider delegateFieldValueProvider;

    public SaveFieldValueProvider(IFieldValueProvider delegateFieldValueProvider) {
        this.delegateFieldValueProvider = delegateFieldValueProvider;
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Boolean boolValue = delegateFieldValueProvider.getBoolValue(field);
        if (boolValue == null) {
            throw new RuntimeException(String.format("cannot resolve %s: %s", field.eClass()
                .getName(), createFieldIdentifier(field)));
        }
        return boolValue;
    }

    @Override
    public Double getDoubleValue(Field field) {
        Double doubleValue = delegateFieldValueProvider.getDoubleValue(field);
        if (doubleValue == null) {
            throw new RuntimeException(String.format("cannot resolve %s: %s", field.eClass()
                .getName(), createFieldIdentifier(field)));
        }
        return doubleValue;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        Integer intValue = delegateFieldValueProvider.getIntegerValue(field);
        if (intValue == null) {
            throw new RuntimeException(String.format("cannot resolve %s: %s", field.eClass()
                .getName(), createFieldIdentifier(field)));
        }
        return intValue;
    }

    @Override
    public String getStringValue(Field field) {
        String stringValue = delegateFieldValueProvider.getStringValue(field);
        if (stringValue == null) {
            throw new RuntimeException(String.format("cannot resolve %s: %s", field.eClass()
                .getName(), createFieldIdentifier(field)));
        }
        return stringValue;
    }

    private String createFieldIdentifier(Field field) {
        if (field instanceof Probe) {
            Probe probe = (Probe) field;
            return String.format("%s:%s", probe.getKind(), probe.getIdentifier());
        }

        throw new RuntimeException("unknown field: " + field);
    }
}
