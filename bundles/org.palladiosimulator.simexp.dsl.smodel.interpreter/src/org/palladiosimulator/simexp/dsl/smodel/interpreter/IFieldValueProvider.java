package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;

public interface IFieldValueProvider {
    Boolean getBoolValue(Field field);

    Double getDoubleValue(Field field);

    Integer getIntegerValue(Field field);

    String getStringValue(Field field);
}
