package org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;

public class StaticValueProvider implements IFieldValueProvider {
    private final IFieldValueProvider dynamicValueProvider;
    private final List<Field> dynamicFields;
    private final Map<Field, Object> initialValueMap;

    public StaticValueProvider(IFieldValueProvider dynamicValueProvider, List<? extends Field> dynamicFields) {
        this.dynamicValueProvider = dynamicValueProvider;
        this.dynamicFields = new ArrayList<>(dynamicFields);
        Comparator<Field> comparator = Comparator.comparing(Field::getName);
        this.initialValueMap = new TreeMap<>(comparator);
    }

    public void assignStatic() {
        for (Field field : dynamicFields) {
            Object currentValue = getCurrentValue(field);
            initialValueMap.put(field, currentValue);
        }
    }

    private Object getCurrentValue(Field field) {
        DataType dataType = field.getDataType();
        switch (dataType) {
        case BOOL:
            return dynamicValueProvider.getBoolValue(field);
        case INT:
            return dynamicValueProvider.getIntegerValue(field);
        case DOUBLE:
            return dynamicValueProvider.getDoubleValue(field);
        case STRING:
            return dynamicValueProvider.getStringValue(field);
        default:
            throw new RuntimeException("Unsupported type: " + dataType);
        }
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Object initialValue = initialValueMap.get(field);
        return (Boolean) initialValue;
    }

    @Override
    public Double getDoubleValue(Field field) {
        Object initialValue = initialValueMap.get(field);
        return (Double) initialValue;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        Object initialValue = initialValueMap.get(field);
        return (Integer) initialValue;
    }

    @Override
    public String getStringValue(Field field) {
        Object initialValue = initialValueMap.get(field);
        return (String) initialValue;
    }
}
